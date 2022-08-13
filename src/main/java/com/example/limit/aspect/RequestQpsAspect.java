package com.example.limit.aspect;

import com.example.limit.annotation.RequestLimit;
import com.example.limit.exception.MyException;
import com.example.limit.factory.LimitStrategyFactory;
import com.example.limit.service.LimitService;
import com.example.limit.utils.Common;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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

    /** 滑动时间窗口限流 */
//    @Qualifier("slidingTimeWindowLimitServiceImpl")
//    @Autowired
//    private LimitService limitService;

    /** 固定时间窗口限流 */
//    @Qualifier("fixedTimeWindowLimitServiceImpl")
//    @Autowired
//    private LimitService fixedTimeWindowLimitServiceImpl;

    @Pointcut(value = "@annotation(requestLimit)", argNames = "requestLimit")
    public void requestQpsPointcut(RequestLimit requestLimit) {
    }

    @Around(value = "requestQpsPointcut(requestLimit)", argNames = "pjp, requestLimit")
    public Object doAround(ProceedingJoinPoint pjp,  RequestLimit requestLimit) throws Throwable {
        int requestCap = requestLimit.requestCap() == 0 ? Common.REQUEST_CAP : requestLimit.requestCap();
        long millisecond = requestLimit.time() == 0 ? Common.MILLISECOND : requestLimit.time();

        Signature signature = pjp.getSignature();
        // 调用限流方法; redis 键是 前缀+类名+方法名；对每个接口单独限流
        // 使用工厂模式获取策略，替换 if else
        LimitService limitService = LimitStrategyFactory.getLimitService(requestLimit.type().getValue());
        boolean isLimit = limitService.limit(requestCap,
                    millisecond,
                    requestLimit.timeUnit(),
                    signature.getDeclaringTypeName() + "." + signature.getName());
//        if (LimitTypeEnum.FIXED_TIME_WINDOW.equals(requestLimit.type())) {
//            // 固定时间窗口算法
//            isLimit = fixedTimeWindowLimitServiceImpl.limit(requestCap,
//                    millisecond,
//                    requestLimit.timeUnit(),
//                    signature.getDeclaringTypeName() + "." + signature.getName());
//        } else if (LimitTypeEnum.SLIDING_TIME_WINDOW.equals(requestLimit.type())) {
//            // 滑动时间窗口算法
//            isLimit = limitService.limit(requestCap,
//                    millisecond,
//                    requestLimit.timeUnit(),
//                    signature.getDeclaringTypeName() + "." + signature.getName());
//        }

        // true-限流，false-请求可通过
        if (isLimit) {
            throw new MyException(HttpStatus.BAD_REQUEST.value(), "服务器繁忙，请稍后访问！");
        }
        // 调用被代理对象的方法
        Object[] args = pjp.getArgs();
        return pjp.proceed(args);
    }

}
