package com.dosion.noisense.web.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user activity statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 활동 통계 정보")
public class UserActivityStatsDto {

  @Schema(description = "사용자가 받은 총 게시글 공감(좋아요) 수")
  private Long totalLikes;

  @Schema(description = "사용자 게시글의 총 댓글 수")
  private Long totalComments;

  @Schema(description = "사용자의 자치구(한글명)")
  private String autonomousDistrictKo;

  @Schema(description = "사용자의 행정동(한글명)")
  private String administrativeDistrictKo;
}
