package com.dosion.noisense.web.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "자치구 시간대별 소음 평균 정보")
public class DistrictNoiseHourlyDto {

  @Schema(description = "시간대 (0~23)")
  private Integer hour;

  @Schema(description = "전일 평균 소음 (dB)")
  private BigDecimal avgDay;

  @Schema(description = "최근 7일 평균 소음 (dB)")
  private BigDecimal avgWeek;

  @Schema(description = "최근 30일 평균 소음 (dB)")
  private BigDecimal avgMonth;
}
