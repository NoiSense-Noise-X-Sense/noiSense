package com.dosion.noisense.web.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

  @Schema(description = "회원명(아이디)")
  private String userNm;

  @Schema(description = "회원닉네임")
  private String nickname;

  @Schema(description = "회원이메일")
  private String email;

}
