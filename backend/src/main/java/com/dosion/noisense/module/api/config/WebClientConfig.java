package com.dosion.noisense.module.api.config;


import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient.Builder webClientBuilder() {

    // 버퍼 사이즈 늘리기
    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
      .codecs(configurer ->
        // 기본 버퍼 사이즈(256KB)를 10MB로 늘립니다.
        configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
      .build();

    // 타임아웃 설정을 위한 HttpClient
    HttpClient httpClient = HttpClient.create()
      // 연결에 소요되는 최대 시간 (5초)
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)

      // 전체 응답을 받는 데까지 걸리는 최대 시간 (60초)
      .responseTimeout(Duration.ofSeconds(60))
      .doOnConnected(conn -> conn
        // 각 데이터 조각을 읽는 사이의 최대 시간 (60초)
        .addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
        // 쓰기 작업의 최대 시간 (60초)
        .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS)));

    return WebClient.builder()
      .exchangeStrategies(exchangeStrategies)
      .baseUrl("http://openapi.seoul.go.kr:8088") // 기본 URL 설정
      .defaultHeader("User-Agent", "NoisenseApp/1.0") // 모든 요청에 포함될 기본 헤더
      .clientConnector(new ReactorClientHttpConnector(httpClient)); // 타임아웃 설정 적용
  }

}
