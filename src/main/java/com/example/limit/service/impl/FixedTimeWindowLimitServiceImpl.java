package com.example.limit.service.impl;

import com.example.limit.enums.LimitTypeEnum;
import com.example.limit.service.LimitService;
import com.example.limit.utils.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/18 - 21:42
 *
 * redis实现限流
 *   固定时间窗口算法
 *      在固定的时间内出现流量溢出可以立即做出限流。每个时间窗口不会相互影响
 *      在时间单元内保障系统的稳定。保障的时间单元内系统的吞吐量上限
 *
 * 多个实现类时，使用@Primary注解标注哪个为主
 */
//@Primary
@Slf4j
@Service
public class FixedTimeWindowLimitServiceImpl implements LimitService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean limit(int requestCap, long time, TimeUnit timeUnit, String key) {
        log.info("固定时间窗口算法限流,requestCap:{},time:{}", requestCap, time);

        // redis的原子类，保证原子性
        RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(Common.LIMIT_REDIS_KEY_FIXED_TIME + ":" + key,
                Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        // 获取原子数（获取的是加1之前的数），并加1 (每请求一次加1)
        int count = redisAtomicInteger.getAndIncrement();
        // 每个时间窗口的开始时设置过期时间 （即设置固定时间窗口）
        if (count == 0) {
            redisAtomicInteger.expire(time, timeUnit);
        }
        // 判断是否已经达到请求上限 (比如设置上限为1000，当第1000个请求进来时可以访问，第1001个请求拒绝访问);此处减1是因为count获取的是加1之前的数
        return count > (requestCap - 1);
    }

    @Override
    public int getSupportedType() {
        return LimitTypeEnum.FIXED_TIME_WINDOW.getValue();
    }

}
