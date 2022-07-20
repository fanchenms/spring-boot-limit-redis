package com.example.limit.service;

import java.util.concurrent.TimeUnit;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/18 - 21:33
 */
public interface LimitService {

    /**
     * 执行限制方法
     * @param requestCap 时间窗口内请求上限
     * @param time 时间窗口长度
     * @param timeUnit 时间单位
     * @param key 键（例如 redis 键）
     * @return true-执行限制，false-不限制
     */
    boolean limit(int requestCap, long time, TimeUnit timeUnit, String key);

}
