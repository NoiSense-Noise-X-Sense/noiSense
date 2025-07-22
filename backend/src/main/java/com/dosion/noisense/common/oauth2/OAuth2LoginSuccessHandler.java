package com.dosion.noisense.common.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {


  @Value("${aws.ec2ip.elastic}")
  private String awsEc2IP;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication)
      throws IOException, ServletException {

    DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

    Map<String, Object> attributes = oAuth2User.getAttributes();


    String accessToken = (String) attributes.get("accessToken");
    String refreshToken = (String) attributes.get("refreshToken");
    String name = (String) attributes.get("name");
    String nickname = (String) attributes.get("nickname");

    log.info("[OAuth2_LOG][Success]" + " 소셜 로그인 시도한 name ={}, nickname ={} ", name, nickname);

    Long id = null;
    Object idObj = attributes.get("id");
    if (idObj != null) {
      id = Long.valueOf(idObj.toString());
    }
    log.info("[OAuth2_LOG] id : {}" , id);

    Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(60 * 10); // 10분
    response.addCookie(accessTokenCookie);

    Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(60 * 60 * 24); // 1일
    response.addCookie(refreshTokenCookie);


    // 로그인 성공
    log.info("login redirect url : {}",  awsEc2IP + ":3000/");
    String redirectUrl = String.format(awsEc2IP + ":3000/");
    response.sendRedirect(redirectUrl);
  }
}
