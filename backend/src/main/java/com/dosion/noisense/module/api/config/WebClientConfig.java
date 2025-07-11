package com.dosion.noisense.module.api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
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
      .responseTimeout(Duration.ofSeconds(10)) // 응답 타임아웃
      .responseTimeout(Duration.ofSeconds(10)); // 연결 타임아웃

    return WebClient.builder()
      .exchangeStrategies(exchangeStrategies)
      .baseUrl("http://openapi.seoul.go.kr:8088") // 기본 URL 설정
      .defaultHeader("User-Agent", "NoisenseApp/1.0") // 모든 요청에 포함될 기본 헤더
      .clientConnector(new ReactorClientHttpConnector(httpClient)); // 타임아웃 설정 적용
  }

}
