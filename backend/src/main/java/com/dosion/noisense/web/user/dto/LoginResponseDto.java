package com.dosion.noisense.web.user.dto;

import com.dosion.noisense.module.auth.entity.Auth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

  @Schema(description = "토큰타입")
  private String tokenType;
  @Schema(description = "엑세스 토큰")
  private String accessToken;
  @Schema(description = "리프레시 토큰")
  private String refreshToken;
  @Schema(description = "회원아이디 (pk)")
  private Long userId;

  public LoginResponseDto(Auth auth) {
    this.tokenType = auth.getTokenType();
    this.accessToken = auth.getAccessToken();
    this.refreshToken = auth.getRefreshToken();
    this.userId = auth.getId();
  }
}
