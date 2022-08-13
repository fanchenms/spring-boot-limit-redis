package com.example.limit.service.impl;

import com.example.limit.enums.LimitTypeEnum;
import com.example.limit.service.LimitService;
import com.example.limit.utils.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/19 - 2:45
 *
 * 限流
 *  滑动时间窗口算法
 *   内部抽象一个滑动的时间窗，将时间更加小化。存在边界的问题更加小。
 */
@Primary
@Slf4j
@Service
public class SlidingTimeWindowLimitServiceImpl implements LimitService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean limit(int requestCap, long time, TimeUnit timeUnit, String key) {
        log.info("滑动时间窗口算法限流,requestCap:{},time:{}", requestCap, time);

        // redis 键是 前缀+类名+方法名；对每个接口单独限流
        String redisKey = Common.LIMIT_REDIS_KEY_SLIDING_TIME + key;
        // 当前时间戳，每当一个请求过来很大可能会变
        long currentTimeMillis = System.currentTimeMillis();
        // 传进来的时间窗口的时间戳，固定不变
        long interMills = TimeUnit.MILLISECONDS.convert(time, timeUnit);
        // 时间窗口与当前时间相差的时间戳
        long differenceMills = currentTimeMillis - interMills;
        /**
         * |-------------------------------------------------|-------------------|
         *                    两时间戳之差                        时间窗口的时间戳    当前时间戳
         *
         * |---------------------------------------------------|-------------------|
         * |------------------------------------------------------|-------------------|
         * |----------------------------------------------------------|-------------------|
         * 随着时间的推移，时间窗口的时间戳不变，我们要取的就是这段范围内的权重的zset的总数，即这段时间窗口的总数，
         * 它相比于固定时间窗口算法，虽然看似时间也是固定的，但是它会不断往前推移，所以时间更加细粒度化，某段时
         * 间内能承载更多的请求
         */
        // 获取从 differenceMills 到 currentTimeMillis 权重之间的总数，即这段时间内的请求数
        Long count = redisTemplate.opsForZSet().count(redisKey, differenceMills, currentTimeMillis);
        // 如果超出请求上限，就限流
        if (Objects.nonNull(count) && count > requestCap) {
            return true;
        }
        // 使用 redis 的 zset 存储, 键、值、权重;键相同值不同就是不同的元素; 权重存储的是当前时间戳，可能每次请求过来都不一样
        redisTemplate.opsForZSet().add(redisKey,
                UUID.randomUUID().toString(),
                currentTimeMillis);
        // 设置过期时间
        redisTemplate.expire(redisKey, time, timeUnit);
        return false;
    }

    @Override
    public int getSupportedType() {
        return LimitTypeEnum.SLIDING_TIME_WINDOW.getValue();
    }

}
