package com.example.limit.aspect;

import com.example.limit.annotation.RequestQps;
import com.example.limit.exception.MyException;
import com.example.limit.service.LimitService;
import com.example.limit.utils.Common;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/18 - 21:02
 */
@Aspect
@Component
public class RequestQpsAspect {

    @Autowired
    private LimitService limitService;

    @Pointcut(value = "@annotation(requestQps)", argNames = "requestQps")
    public void requestQpsPointcut(RequestQps requestQps) {
    }

    @Around(value = "requestQpsPointcut(requestQps)", argNames = "pjp, requestQps")
    public Object doAround(ProceedingJoinPoint pjp,  RequestQps requestQps) throws Throwable {
        int requestCap = requestQps.requestCap() == 0 ? Common.REQUEST_CAP : requestQps.requestCap();
        long millisecond = requestQps.time() == 0 ? Common.MILLISECOND : requestQps.time();
        // 调用限流方法
        boolean isLimit = limitService.limit(requestCap, millisecond, requestQps.timeUnit());
        // true-限流，false-请求可通过
        if (isLimit) {
            throw new MyException(HttpStatus.BAD_REQUEST.value(), "服务器繁忙，请稍后访问！");
        }
        // 调用被代理对象的方法
        Object[] args = pjp.getArgs();
        return pjp.proceed(args);
    }

}
