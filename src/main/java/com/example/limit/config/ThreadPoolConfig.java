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
     * 线程池任务调度器
     * @return
     */
    @Bean()
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 线程池大小
        scheduler.setPoolSize(20);
        // 设置线程名称前缀
        scheduler.setThreadNamePrefix("taskExecutor-");
        // 设置等待任务在关机时完成
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 设置等待终止秒数
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }

    /**
     * 线程池
     * @return
     */
    //@Bean()
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(2,
                Runtime.getRuntime().availableProcessors(),
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

}
