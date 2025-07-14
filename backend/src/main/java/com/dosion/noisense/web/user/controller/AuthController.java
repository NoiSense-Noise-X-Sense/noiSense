package com.dosion.noisense.web.user.controller;

import com.dosion.noisense.module.auth.service.AuthService;
import com.dosion.noisense.web.user.dto.LoginRequestDto;
import com.dosion.noisense.web.user.dto.LoginResponseDto;
import com.dosion.noisense.web.user.dto.SignUpRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth-Controller", description = "권한관리")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "회원 가입", description = "새로운 사용자를 등록합니다")
  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto signUpRequestDTO) {
    try {
      authService.signUp(signUpRequestDTO);
      return ResponseEntity.ok("회원가입 성공");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); //401
    }
  }

  @Operation(summary = "로그인", description = "사용자 인증 후 토큰을 발급합니다")
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDTO) {
    LoginResponseDto loginResponseDTO = authService.login(loginRequestDTO);
    return ResponseEntity.ok(loginResponseDTO);
  }

  @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다")
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestHeader(value = "Authorization", required = false)
  String authorizationHeader, HttpServletRequest request) {
    String refreshToken = null;
    // 1. 쿠키에서 찾기
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("refreshToken".equals(cookie.getName())) {
          refreshToken = cookie.getValue();
        }
      }
    }
    // 2. Authorization 헤더 찾기
    if (refreshToken == null && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      refreshToken = authorizationHeader.replace("Bearer ", "").trim();
    }
    if (refreshToken == null || refreshToken.isEmpty()) {
      throw new IllegalArgumentException("리프레시 토큰이 없습니다.");
    }
    String newAccessToken = authService.refreshToken(refreshToken);
    //json 객체로 변환하여 front에 내려주기
    Map<String, String> res = new HashMap<>();
    res.put("accessToken", newAccessToken);
    res.put("refreshToken", refreshToken);

    return ResponseEntity.status(HttpStatus.OK).body(res);
  }

  @Operation(summary = "로그아웃", description = "사용자의 인증 토큰을 무효화하고 쿠키를 삭제합니다")
  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {

    Cookie accessTokenCookie = new Cookie("accessToken", null);
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(0); // 즉시 만료!

    Cookie refreshTokenCookie = new Cookie("refreshToken", null);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(0);

    response.addCookie(accessTokenCookie);
    response.addCookie(refreshTokenCookie);

    return ResponseEntity.ok().body("로그아웃 완료(쿠키삭제)");
  }

}
