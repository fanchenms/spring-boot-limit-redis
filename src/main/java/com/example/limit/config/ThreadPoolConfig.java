package com.example.limit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.*;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/20 - 21:34
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 漏桶滴水线程池任务调度器
     * @return
     */
    @Bean(name = "leakyBucketPopThreadPoolScheduler")
    public ThreadPoolTaskScheduler leakyBucketPopThreadConfig() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("漏桶滴水线程");
        return scheduler;
    }

    /**
     * 线程池
     * @return
     */
    //@Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(2,
                Runtime.getRuntime().availableProcessors(),
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

}
