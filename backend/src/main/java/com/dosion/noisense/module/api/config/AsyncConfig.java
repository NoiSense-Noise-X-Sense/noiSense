package com.dosion.noisense.module.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {

  @Bean(name = "batchTaskExecutor")
  public Executor batchTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(10); // 실행할 스레드 갯수
    executor.setMaxPoolSize(10); // 실행 제한 할 갯수
    executor.setQueueCapacity(100); // 대기 큐 크기
    executor.setThreadNamePrefix("batch-fetch-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
