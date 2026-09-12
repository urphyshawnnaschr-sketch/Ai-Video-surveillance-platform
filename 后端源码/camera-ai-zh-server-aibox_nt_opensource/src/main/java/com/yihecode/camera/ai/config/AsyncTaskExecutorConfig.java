package com.yihecode.camera.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
* Async Task Thread Pool
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Configuration
public class AsyncTaskExecutorConfig {

    //Block Queue
private static final int workQueue = 20;

// Thread empty Idle after Store Work Hour long
private static final int keepAliveTime = 30;

// Cpu Core Number
private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

// Core Core Thread Count big small
private static final int corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4));

// Thread Pool most big Capacity Include Thread Number
private static final int maxPoolSize = CPU_COUNT * 2 + 1;

@Bean("asyncTaskExecutor")
public ThreadPoolTaskExecutor asyncTaskExecutor() {
ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
threadPoolTaskExecutor.setThreadNamePrefix("asyncTaskExecutor-");// Thread front Concat
threadPoolTaskExecutor.setCorePoolSize(corePoolSize);// Core Core Thread Number
threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);// most big Thread Number
threadPoolTaskExecutor.setQueueCapacity(workQueue);// Wait Queue
threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveTime);// Thread Pool Maintenance Thread The Allow empty Idle Time, form Bit for s
threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());// Thread Pool for Reject Task (no Thread can Use) Process Strategy
threadPoolTaskExecutor.initialize();
return threadPoolTaskExecutor;
}
}
