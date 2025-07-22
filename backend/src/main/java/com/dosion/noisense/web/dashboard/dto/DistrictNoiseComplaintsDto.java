package com.dosion.noisense.web.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "연도별 소음 민원 건수")
public class DistrictNoiseComplaintsDto {

  @Schema(description = "연도")
  private Integer year;

  @Schema(description = "민원 건수")
  private Integer count;
}
