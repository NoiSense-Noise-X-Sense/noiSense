package com.dosion.noisense.common.security.jwt;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtKey {

  @Value("${jwt.secretKey}")
  private String secretKey;

  @Bean
  public SecretKey secretKey() {
    byte[] keyBytes = secretKey.getBytes();
    return new SecretKeySpec(keyBytes, "HmacSHA512");
  }

}
