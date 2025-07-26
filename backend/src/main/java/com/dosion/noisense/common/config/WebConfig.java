package com.dosion.noisense.common.config; // 패키지는 실제 프로젝트 구조에 맞게!

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
      , awsEc2IPDomain)
      .allowedMethods(
        HttpMethod.GET.name(),
        HttpMethod.POST.name(),
        HttpMethod.PUT.name(),
        HttpMethod.DELETE.name(),
        HttpMethod.OPTIONS.name()
      )
      .allowedHeaders("*")
      // 2. 클라이언트에서 접근해야 할 응답 헤더만 명시 (예: JWT 토큰)
      .exposedHeaders("Authorization")
      .allowCredentials(true);
  }
}
