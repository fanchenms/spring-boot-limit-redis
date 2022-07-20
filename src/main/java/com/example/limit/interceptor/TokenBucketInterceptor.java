package com.example.limit.interceptor;

import com.example.limit.annotation.RequestLimit;
import com.example.limit.enums.LimitTypeEnum;
import com.example.limit.exception.MyException;
import com.example.limit.utils.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/20 - 3:23
 * 拦截器
 * 使用令牌桶算法进行全局限流
 */
@Slf4j
@Component
public class TokenBucketInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //Method method = handlerMethod.getMethod();
        // 如果方法上没有注解 @RequestLimit 直接放行；此处不能加这段代码，因为该注解可以在类上，对整个类起作用
        //if (!method.isAnnotationPresent(RequestLimit.class)) {
        //    return true;
        //}

        /**
         * 先从类上获取限流注解，如果为空，再从方法上获取，这样的话，即使方法上单独有限流注解，
         * 类上的限流注解也起作用，方法上单独的限流注解走它自己的实现，不受影响。即类上的限流起作用，
         * 方法上的限流也起作用
         *
         * 如果想的是，方法上有了单独的限流注解，就只使用注解在方法上的限流规则，而不使用注解在类上的
         * 限流规则，则先获取方法上的限流注解，如果为空，再去获取类上的限流注解即可，即调换一下位置；
         * 代码如下：
         *
         * // 获取方法上的注解
         * RequestLimit requestLimit = handlerMethod.getMethodAnnotation(RequestLimit.class);
         * // 如果方法上没有 @RequestLimit 注解，则从该类上获取
         * if (Objects.isNull(requestLimit)) {
         *     requestLimit = handlerMethod.getBeanType().getAnnotation(RequestLimit.class);
         * }
         */
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
        // 如果注解的不是令牌桶算法，执行放行
        if (!LimitTypeEnum.TOKEN_BUCKET.equals(requestLimit.type())) {
            return true;
        }
        log.info("令牌桶算法限流");
        // 从令牌桶中获取令牌 （redis的List结构）
        Object token = redisTemplate.opsForList().leftPop(Common.LIMIT_REDIS_KEY_TOKEN_BUCKET);
        // 如果能从令牌桶中获取到令牌，可以访问系统，否则无法通行; 当然也可以对令牌进行校验，防止篡改
        if (Objects.nonNull(token)) {
            return true;
        } else {
            throw new MyException(HttpStatus.BAD_REQUEST.value(), "拒绝访问，请稍后重试！");
        }

    }

}
