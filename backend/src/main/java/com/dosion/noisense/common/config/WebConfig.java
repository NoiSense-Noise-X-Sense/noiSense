package com.dosion.noisense.common.config; // 패키지는 실제 프로젝트 구조에 맞게!

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${aws.awsip}")
  private String awsEc2IP;

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    String awsIp = "http://" + awsEc2IP;

    registry.addMapping("/api/**")
      .allowedOrigins("http://localhost:3000"
      , awsIp
      , awsIp +":3000")
      .allowedMethods("*")
      .allowCredentials(true);
  }
}
