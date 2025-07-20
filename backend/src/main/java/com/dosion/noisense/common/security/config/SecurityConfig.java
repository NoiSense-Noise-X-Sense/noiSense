package com.dosion.noisense.common.security.config;

import com.dosion.noisense.common.oauth2.OAuth2LoginSuccessHandler;
import com.dosion.noisense.common.oauth2.OAuth2LogoutSuccessHandler;
import com.dosion.noisense.common.oauth2.RedisOAuth2AuthorizationRequestRepository;
import com.dosion.noisense.common.security.jwt.JwtTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenFilter jwtTokenFilter;
  private final OAuth2LogoutSuccessHandler oAuth2LogoutSuccessHandler;
  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
  private final OAuth2UserService oAuth2UserService;
  private final RedisTemplate<String, Object> redisTemplate;

  @Bean
  public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
    return new RedisOAuth2AuthorizationRequestRepository(redisTemplate);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
      .cors(Customizer.withDefaults()) // CORS 설정
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/index.html", "/*.html", "/favicon.ico",
          "/css/**", "/fetchWithAuth.js", "/js/**", "/images/**",
          "/.well-known/**", "/error"
          , "/v3/api-docs/**", "/configuration/**"
          , "/exception",
          "/swagger-ui/**",
          "/swagger-ui.html").permitAll()

        .requestMatchers(
          "/api/auth/sign-up",
          "/api/auth/login",
          "/api/auth/logout",
          "/oauth2/**",
          "/login/**",
          "/actuator/prometheus",
          "/api/batch/run-initial-load",
          "/api/dashboard/**"
        ).permitAll()

        .requestMatchers("/api/**").authenticated()
        .anyRequest().permitAll()
      )
      .exceptionHandling(e -> e
        .authenticationEntryPoint((request, response, authException) -> {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
          String url = request.getRequestURI().toString();
          log.warn("[SecurityConfig] Unauthorized =====>>>> : " + url);
        })
        .accessDeniedHandler((request, response, authException) -> {
          response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
          String url = request.getRequestURI().toString();
          log.warn("[SecurityConfig] Forbidden ======>>>> : " + url);
        })
      )
      .oauth2Login(oauth2 -> oauth2
        .loginPage("/index.html")
        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
        .successHandler(oAuth2LoginSuccessHandler)
        .authorizationEndpoint(authorization -> authorization
          .authorizationRequestRepository(authorizationRequestRepository()))
      )
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessHandler(oAuth2LogoutSuccessHandler)
        .permitAll()
      )
      .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  // ✅ CORS Filter 설정
  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 프론트 주소
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(Arrays.asList("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }
}
