package com.example.limit.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/18 - 20:42
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestQps {

    /** 请求上限 */
    int requestCap() default 0;

    /** 时间间隔 */
    long time() default 0;

    /** 时间单位 */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
