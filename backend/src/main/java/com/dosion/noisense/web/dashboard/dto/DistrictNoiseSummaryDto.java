package com.dosion.noisense.web.dashboard.dto;

import com.dosion.noisense.module.dashboard.entity.KeywordCount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "자치구별 소음 요약 정보")
public class DistrictNoiseSummaryDto {

  @Schema(description = "자치구명 (예: 강남구)")
  private String autonomousDistrict;

  @Schema(description = "분석 시작일")
  private LocalDate startDate;

  @Schema(description = "분석 종료일")
  private LocalDate endDate;

  @Schema(description = "한달 평균 소음 (dB)")
  private BigDecimal avgNoise;

  @Schema(description = "소음 집중 시간 (0~23)")
  private Integer peakHour;

  @Schema(description = "소음 집중 시간의 데시벨 (dB)")
  private BigDecimal peakNoise;

  @Schema(description = "소음 안정 시간 (0~23)")
  private Integer calmHour;

  @Schema(description = "소음 안정 시간의 데시벨 (dB)")
  private BigDecimal calmNoise;

  @Schema(description = "상위 키워드 리스트 (감정 키워드)")
  private List<KeywordCount> topKeywords;
}
