package com.dosion.noisense.web.dashboard.controller;

import com.dosion.noisense.module.dashboard.service.DashboardService;
import com.dosion.noisense.web.dashboard.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Noise-Dashboard-Controller", description = "지역별 소음 대시보드")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @Operation(
    summary = "지역별 소음 요약 통계 호출",
    description = "한달 평균 소음, 집중/안정 시간, 키워드 등 요약 데이터를 반환합니다."
  )
  @ApiResponse(
    responseCode = "200",
    description = "성공",
    content = @Content(schema = @Schema(implementation = DistrictNoiseSummaryDto.class))
  )
  @GetMapping("/summary")
  public ResponseEntity<DistrictNoiseSummaryDto> getDistrictSummary(@RequestParam String district) {
    log.info("요약 소음 통계 요청 - district: {}", district);
    return ResponseEntity.ok(dashboardService.getLatestSummary(district));
  }


  @Operation(
    summary = "시간대별 소음 평균 정보 조회",
    description = "자치구별 0~23시 시간대별 소음 평균 추이 제공"
  )
  @ApiResponse(
    responseCode = "200",
    description = "성공",
    content = @Content(schema = @Schema(implementation = DistrictNoiseHourlyDto.class))
  )
  @GetMapping("/hourly")
  public ResponseEntity<List<DistrictNoiseHourlyDto>> getHourlyNoise(@RequestParam String district) {
    log.info("시간대별 소음 요청 - district: {}", district);
    return ResponseEntity.ok(dashboardService.getHourlyNoise(district));
  }


  @Operation(summary = "연도별 평균 소음 비교", description = "서울시 vs 자치구 평균 소음을 연도별로 조회합니다.")
  @ApiResponse(
    responseCode = "200",
    description = "성공",
    content = @Content(schema = @Schema(implementation = DistrictNoiseYearlyDto.class))
  )
  @GetMapping("/yearly")
  public ResponseEntity<List<DistrictNoiseYearlyDto>> getYearlyNoise(@RequestParam String district) {
    log.info("연도별 소음 통계 요청 - district: {}", district);
    return ResponseEntity.ok(dashboardService.getYearlyNoise(district));
  }

  @Operation(
    summary = "행정동별 평균 소음 조회",
    description = "자치구 내 행정동별 평균 소음 정보를 반환합니다."
  )
  @ApiResponse(
    responseCode = "200",
    description = "성공",
    content = @Content(schema = @Schema(implementation = DistrictNoiseZoneDto.class))
  )
  @GetMapping("/zone")
  public ResponseEntity<List<DistrictNoiseZoneDto>> getZoneNoise(@RequestParam String district) {
    log.info("행정동별 소음 통계 요청 - district: {}", district);
    return ResponseEntity.ok(dashboardService.getZoneNoise(district));
  }


  @Operation(summary = "연도별 소음 민원 추이", description = "자치구 기준 최근 5년간 민원 건수를 반환합니다.")
  @ApiResponse(
    responseCode = "200",
    description = "성공",
    content = @Content(schema = @Schema(implementation = DistrictNoiseComplaintsDto.class))
  )
  @GetMapping("/complaints")
  public ResponseEntity<List<DistrictNoiseComplaintsDto>> getComplaintsByDistrict(@RequestParam String district) {
    log.info("소음 민원 추이 요청 - district: {}", district);
    return ResponseEntity.ok(dashboardService.getComplaintsByDistrict(district));
  }


}
