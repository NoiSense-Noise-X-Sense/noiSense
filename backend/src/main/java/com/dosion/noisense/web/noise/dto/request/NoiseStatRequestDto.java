package com.dosion.noisense.web.noise.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record NoiseStatRequestDto(
  @Schema(description = "시작일시 (예: 2025-06-01T00:00)")
  LocalDateTime startDate,

  @Schema(description = "종료일시 (예: 2025-07-01T00:00)")
  LocalDateTime endDate,

  @Schema(description = "자치구 코드 (전체 조회 시 'all')")
  String autonomousDistrictCode,

  @Schema(description = "행정동 코드 (전체 조회 시 'all')")
  String administrativeDistrictCode,

  @Schema(description = "지역 유형 (FullMap 전용)")
  List<String> regionList
) {}
