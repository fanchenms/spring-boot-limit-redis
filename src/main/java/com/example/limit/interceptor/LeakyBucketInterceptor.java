package com.example.limit.interceptor;

import com.example.limit.annotation.RequestLimit;
import com.example.limit.enums.LimitTypeEnum;
import com.example.limit.exception.MyException;
import com.example.limit.utils.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.UUID;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/20 - 23:36
 * 漏桶算法实现全局限流
 */
@Slf4j
@Component
public class LeakyBucketInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /** 漏桶上限 */
    @Value("${leaky.bucket.capacity}")
    private long capacity = 100;

    /** 固定速率往漏桶中生成的数据数量 */
    @Value("${leaky.bucket.number}")
    private long number = 10;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 从类上获取 @RequestLimit 注解
        RequestLimit requestLimit = handlerMethod.getBeanType().getAnnotation(RequestLimit.class);
        // 如果类上没有该注解，则从方法上获取
        if (Objects.isNull(requestLimit)) {
            requestLimit = handlerMethod.getMethodAnnotation(RequestLimit.class);
        }

        // 如果方法和类上都没有该注解，直接放行
        if (Objects.isNull(requestLimit)) {
            return true;
        }
        // 如果注解的不是漏桶算法，执行放行
        if (!LimitTypeEnum.LEAKY_BUCKET.equals(requestLimit.type())) {
            return true;
        }
        log.info("漏桶算法限流");

        // 当前漏桶中容量
        Long curLeakyBucketSize = redisTemplate.opsForList().size(Common.LIMIT_REDIS_KEY_LEAKY_BUCKET);
        // 漏桶已满，拒绝访问
        if ((Objects.isNull(curLeakyBucketSize) ? 0 : curLeakyBucketSize) >= capacity) {
            throw new MyException(HttpStatus.BAD_REQUEST.value(), "请求超限，请稍后重试！");
        }
        // 漏桶未满，可放行
        // 进桶中排队；trim()被我们设置成截取抛弃的是左边的数据（即已处理数据），所以新进来的请求需要从右边进入排队等候处理；返回值为添加元素后的list的大小
        Long addedSize = redisTemplate.opsForList().rightPush(Common.LIMIT_REDIS_KEY_LEAKY_BUCKET,
                UUID.randomUUID().toString().replace("-", ""));
        // 为了防止并发场景。这里添加完成之后也要验证。即使这样本段代码在高并发也有问题(经测试，百万级别没有出现问题)
        // 添加进list后的大小如果超出了请求上限，需要把新进来的最后的所有请求拒绝掉（理论上是一个，高并发场景可能不止一个）
        if ((Objects.isNull(addedSize) ? 0 : addedSize) > capacity) {
            // 拒绝的是最右边的所有的请求，由于高并发可能不止一个
            redisTemplate.opsForList().trim(Common.LIMIT_REDIS_KEY_LEAKY_BUCKET, 0, capacity - 1);
            throw new MyException(HttpStatus.BAD_REQUEST.value(), "请求超限，请稍后重试！");
        }

        return true;
    }

    /**
     * 漏桶匀速滴水，即从list中取出数据
     * 启动一个任务调用器线程 （此处用注解方式启动一个定时任务也可以）
     */
    @PostConstruct
    public void popData() {
        String redisKey = Common.LIMIT_REDIS_KEY_LEAKY_BUCKET;
        // 定时任务
        threadPoolTaskScheduler.scheduleAtFixedRate(() -> {
            if (redisTemplate.hasKey(redisKey)
                    && redisTemplate.opsForList().size(redisKey) > 0) {
                /**
                 * trim()方法：对一个列表list进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
                 * 从0开始，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
                 */
                redisTemplate.opsForList().trim(redisKey, number, -1);
            }
        }, 1000);
    }

}
