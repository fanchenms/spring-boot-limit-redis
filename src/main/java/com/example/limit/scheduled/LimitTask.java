package com.example.limit.scheduled;

import com.example.limit.utils.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/19 - 18:28
 */
@Slf4j
@Component
public class LimitTask {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 由于滑动时间窗口算法会产生过多的数据，所以定时清理一些无用的数据
     * 固定时间窗口算法中redis的key有过期时间，不会产生大量数据
     * 每两天的凌晨2点执行一次
     */
    @Scheduled(cron = "* * 2 1/2 * ? ")
    public void deleteSlidingTimeWindowData() {
        long currentTimeMillis = System.currentTimeMillis();
        long dayMillis = TimeUnit.DAYS.toMillis(2);
        // 删除两天前的数据,总数据要大于10000
        if (redisTemplate.hasKey(Common.LIMIT_REDIS_KEY_SLIDING_TIME)
                && redisTemplate.opsForZSet().size(Common.LIMIT_REDIS_KEY_SLIDING_TIME) > 10000) {
            redisTemplate.opsForZSet().removeRangeByScore(Common.LIMIT_REDIS_KEY_SLIDING_TIME,
                    0,
                    currentTimeMillis - dayMillis);
        }
    }

}
