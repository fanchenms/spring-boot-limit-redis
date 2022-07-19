package com.example.limit.service.impl;

import com.example.limit.service.LimitService;

import java.util.concurrent.TimeUnit;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/19 - 21:10
 *
 * 令牌桶算法
 */
public class TokenBucketLimitServiceImpl implements LimitService {

    @Override
    public boolean limit(int requestCap, long time, TimeUnit timeUnit) {
        return false;
    }

}
