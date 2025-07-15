package com.dosion.noisense.common.security.jwt;

import com.dosion.noisense.common.security.core.CustomUserDetailsService;
import com.dosion.noisense.common.threadlocal.TraceIdHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailsService customUserDetailsService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/css/")
      || path.startsWith("/js/")
      || path.startsWith("/images/")
      || path.equals("/")
      || path.equals("/index.html")
      || path.endsWith(".html")
      || path.startsWith("/favicon.ico")
      || path.startsWith("/api/auth/")
      || path.startsWith("/error")
      || path.startsWith("/fetchWithAuth.js")
      || path.startsWith("/swagger-ui")
      || path.startsWith("/v3/api-docs")
      || path.startsWith("/actuator/prometheus");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain) throws ServletException, IOException {

    try {
      String traceId = UUID.randomUUID().toString().substring(0, 8);
      TraceIdHolder.set(traceId);

      String accessToken = getTokenFromRequest(request);

      if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(accessToken);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        String url = request.getRequestURI();
        log.info("현재 들어온 HTTP 요청 = {}", url);
        log.info("✅ 토큰 인증 성공: {}", accessToken);
        log.info("✅ 토큰 인증 성공 url: {}", url);
      } else {
        String url = request.getRequestURI();
        log.info("❌ 토큰 없음 또는 유효하지 않음: {}", accessToken);
        log.info("❌ 토큰 없음 또는 유효하지 않음 url: {}", url);
      }

      filterChain.doFilter(request, response);
    } finally {
      TraceIdHolder.clear();
    }
  }

  private String getTokenFromRequest(HttpServletRequest request) {
    String token = null;
    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      token = bearerToken.substring(7);
    }

    if (token == null && request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("accessToken".equals(cookie.getName())) {
          token = cookie.getValue();
          break;
        }
      }
    }
    return token;
  }

  private UsernamePasswordAuthenticationToken getAuthentication(String token) {
    Long userid = jwtTokenProvider.getUserIdFromToken(token);
    UserDetails userDetails = customUserDetailsService.loadUserByUserId(userid);
    return new UsernamePasswordAuthenticationToken(
      userDetails,
      null,
      userDetails.getAuthorities()
    );
  }
}
