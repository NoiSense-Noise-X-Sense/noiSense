package com.dosion.noisense.web.user.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원 정보")
public class UserDto {

  @Schema(description = "회원 아이디(userId)")
  private Long id;

  @Schema(description = "회원명") //@kakao
  private String userNm;

  @Schema(description = "회원 닉네임")
  private String nickname;

  @Schema(description = "이메일")
  private String email;

  @Schema(description = "회원 자치구")
  private String autonomousDistrict;

  @Schema(description = "회원 행정동")
  private String administrativeDistrict;

  @Schema(description = "회원 생성일")
  private LocalDateTime createdDate;

  @Schema(description = "회원정보 수정일")
  private LocalDateTime modifiedDate;
}
