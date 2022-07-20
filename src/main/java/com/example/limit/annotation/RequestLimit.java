package com.example.limit.annotation;

import com.example.limit.enums.LimitTypeEnum;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/18 - 20:42
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {

    /** 请求上限 */
    int requestCap() default 0;

    /** 时间间隔 */
    long time() default 0;

    /** 时间单位 */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /** 限流类型 */
    LimitTypeEnum type() default LimitTypeEnum.SLIDING_TIME_WINDOW;
}
