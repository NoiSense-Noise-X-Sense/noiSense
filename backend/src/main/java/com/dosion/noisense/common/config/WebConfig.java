package com.dosion.noisense.common.config; // 패키지는 실제 프로젝트 구조에 맞게!

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${aws.ec2ip.private}")
  private String awsEc2IP;

  @Value("${aws.ec2ip.domain}")
  private String awsEc2IPDomain;

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    registry.addMapping("/api/**")
      .allowedOrigins("http://localhost:3000"
      , awsEc2IP
      , awsEc2IP +":3000"
      , awsEc2IPDomain
      , awsEc2IPDomain +":3000")
      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
      .allowCredentials(true);
  }
}
