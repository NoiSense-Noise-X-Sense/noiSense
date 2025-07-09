package com.dosion.noisense.common.config;

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
        //인증 필요없음
        .requestMatchers("/","/index.html", "/*.html", "/favicon.ico",
          "/css/**", "/fetchWithAuth.js","/js/**", "/images/**",
          "/.well-known/**", "/error"
          , "/v3/api-docs/**", "/configuration/**"
          ,"/exception",
          "/swagger-ui/**",
          "/swagger-ui.html").permitAll() // 정적 리소스 누구나 접근

        .anyRequest().permitAll()
      )
      .csrf(csrf -> csrf.disable())// csrf disable
      .formLogin( form -> form.disable()) // form login 기본값 사용하려면 .formLogin(Customizer.withDefaults())
      .oauth2Login(oauth2 -> oauth2.disable()); // oauth2 login disable

    return http.build();
  }
}
