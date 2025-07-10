package com.dosion.noisense.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "소음 분석 리포트 DTO")
public class SensorDataDTO {

  @Schema(description = "ID", example = "1")
  @NotBlank
  private Long id;

  @Schema(description = "데이터 측정 시간", example = "2023-10-27T10:00:00")
  @NotBlank
  private LocalDateTime sensingTime;

  @Schema(description = "자치구", example = "강남구")
  @NotBlank
  private String autonomousDistrict;

  @Schema(description = "행정동", example = "역삼동")
  @NotBlank
  private String administrativeDistrict;

  @Schema(description = "최대 소음 (dB)", example = "85.5")
  private Double maxNoise;

  @Schema(description = "평균 소음 (dB)", example = "65.0")
  private Double avgNoise;

  @Schema(description = "최소 소음 (dB)", example = "45.5")
  private Double minNoise;

  @Schema(description = "최고 온도 (°C)", example = "25.5")
  private Double maxTemp;

  @Schema(description = "평균 온도 (°C)", example = "23.0")
  private Double avgTemp;

  @Schema(description = "최저 온도 (°C)", example = "21.5")
  private Double minTemp;

  @Schema(description = "최고 습도 (%)", example = "80.0")
  private Double maxHumidity;

  @Schema(description = "평균 습도 (%)", example = "60.5")
  private Double avgHumidity;

  @Schema(description = "최저 습도 (%)", example = "55.0")
  private Double minHumidity;

}
