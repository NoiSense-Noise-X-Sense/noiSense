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
    // 동시에 실행할 스레드 수. API 서버에 과부하를 주지 않도록 5~10개 정도 추천
    // 이 부분에서 더 빠르게도 가능
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100); // 대기 큐 크기
    executor.setThreadNamePrefix("batch-fetch-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
