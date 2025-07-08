package com.dosion.noisense.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(auth -> auth
        .anyRequest().permitAll()
      )
      .csrf(csrf -> csrf.disable())// csrf disable
      .formLogin( form -> form.disable()) // form login 기본값 사용하려면 .formLogin(Customizer.withDefaults())
      .oauth2Login(oauth2 -> oauth2.disable()); // oauth2 login disable

    return http.build();
  }
}
