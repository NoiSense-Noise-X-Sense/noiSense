package com.dosion.noisense.common.oauth2;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class OAuth2LogoutSuccessHandler implements LogoutSuccessHandler {

  // 카카오 REST API 키 (환경변수나 properties에서 가져오세요)
  @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
  private String kakaoClientId;

  @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
  private String kakaoLogoutRedirectUri;
//  private final String kakaoLogoutRedirectUri = "http://localhost:8080/login"; // 앱 환경에 맞게 변경


  @Override
  public void onLogoutSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    String redirectUrl = "/index.html";

    if (authentication!=null && authentication.getPrincipal() instanceof DefaultOAuth2User auth2User){

      Map<String,Object> attributes = auth2User.getAttributes();
      log.info("attribute ===="+ attributes.toString());

      Object email = attributes.get("email");
      if (email!=null && email.toString().endsWith("@gmail.com")){
      log.info("구글 로그아웃입니다.");
        redirectUrl = "https://accounts.google.com/Logout";
      }

      // 카카오 로그인 사용자인 경우 (attributes에 'id' 키가 있음)
      else if (attributes.containsKey("id")){
        log.info("카카오 로그아웃입니다.");
        redirectUrl = "https://kauth.kakao.com/oauth/logout?client_id=" + kakaoClientId
            + "&logout_redirect_uri=" + kakaoLogoutRedirectUri;
      }
    }

    // 최종적으로 redirectUrl로 리디렉트
    response.sendRedirect(redirectUrl);

  }
}
