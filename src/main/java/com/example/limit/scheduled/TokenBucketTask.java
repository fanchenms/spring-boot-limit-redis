package com.example.limit.scheduled;

import com.example.limit.utils.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/19 - 21:17
 */
@Slf4j
@Component
public class TokenBucketTask {

    @Value("${token.bucket.capacity}")
    private long capacity = 100;
    @Value("${token.bucket.number}")
    private long number = 10;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 令牌桶算法
     * 分发令牌
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void distributionToken() {
        String redisKey = Common.LIMIT_REDIS_KEY_TOKEN_BUCKET;
        Long size = redisTemplate.opsForList().size(redisKey);
        long no = Objects.isNull(size) ? 0 : size;
        if (no < capacity) {
            for (int i = 0; i < number; i++) {
                Long listSize = redisTemplate.opsForList().size(redisKey);
                long n = Objects.isNull(listSize) ? 0 : listSize;
                if (n < capacity) {
                    // 往桶中放入令牌
                    redisTemplate.opsForList().rightPush(Common.LIMIT_REDIS_KEY_TOKEN_BUCKET,
                            UUID.randomUUID().toString().replace("-", ""));
                }
            }
        }
    }

    //@Scheduled(cron = "0/1 * * * * ?")
    public void printSize() {
        log.info("令牌桶令牌数量：{}", redisTemplate.opsForList().size(Common.LIMIT_REDIS_KEY_TOKEN_BUCKET));
    }

}
