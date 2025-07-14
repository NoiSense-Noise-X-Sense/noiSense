package com.dosion.noisense.web.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

  @Schema(description = "회원명")
  private String userNm;

  @Schema(description = "회원 닉네임")
  private String nickname;
//  private String password;
}
