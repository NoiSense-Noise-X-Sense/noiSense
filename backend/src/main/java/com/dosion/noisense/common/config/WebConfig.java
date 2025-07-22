package com.dosion.noisense.common.config; // 패키지는 실제 프로젝트 구조에 맞게!

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
      .allowedOrigins("http://localhost:3000"
      ,"http://172.31.39.164")
      .allowedMethods("*")
      .allowCredentials(true);
  }
}
