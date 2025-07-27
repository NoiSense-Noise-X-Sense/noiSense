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
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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



  private static final String[] PUBLIC_STATIC_RESOURCES = {
    "/", "/index.html", "/*.html", "/favicon.ico", "/css/**",
    "/fetchWithAuth.js", "/js/**", "/images/**", "/.well-known/**", "/error"
  };
  private static final String[] SWAGGER_RESOURCES = {
    "/v3/api-docs/**", "/configuration/**", "/swagger-ui/**", "/swagger-ui.html"
  };
  private static final String[] PUBLIC_API_ROUTES = {
    "/api/auth/**", "/oauth2/**", "/login/**", "/actuator/prometheus",
    "/api/batch/run-initial-load", "/api/dashboard/**", "/api/es/board/frequent-words"
  };

  @Bean
  public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
    return new RedisOAuth2AuthorizationRequestRepository(redisTemplate);
  }




  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
      // CORS 설정
      .cors(Customizer.withDefaults())
      .csrf(AbstractHttpConfigurer::disable)


      .headers(headers -> headers
        .referrerPolicy(policy -> policy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER_WHEN_DOWNGRADE))
      )

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
          "/api/dashboard/**",
          "/api/es/board/frequent-words"
        ).permitAll()

        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/boards", "/api/boards/**").permitAll()
        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/comments", "/api/comments/**").permitAll()
        .requestMatchers(PUBLIC_STATIC_RESOURCES).permitAll()
        .requestMatchers(SWAGGER_RESOURCES).permitAll()
        .requestMatchers(PUBLIC_API_ROUTES).permitAll()
        .requestMatchers("/api/**").authenticated()
        .anyRequest().permitAll() // 나머지 요청은 일단 모두 허용 (필요시 .denyAll() 또는 .authenticated()로 변경)
      )
      .exceptionHandling(e -> e
        .authenticationEntryPoint((request, response, authException) -> {
          log.warn("[SecurityConfig] Unauthorized request to {}", request.getRequestURI(), authException);
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        })
        .accessDeniedHandler((request, response, accessDeniedException) -> {
          log.warn("[SecurityConfig] Forbidden access to {}", request.getRequestURI(), accessDeniedException);
          response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
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
    ).build();
  }


  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setExposedHeaders(Arrays.asList("Authorization"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
