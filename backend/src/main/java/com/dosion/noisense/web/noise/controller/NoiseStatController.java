package com.dosion.noisense.web.noise.controller;

import com.dosion.noisense.module.noise.service.NoiseStatService;
import com.dosion.noisense.module.noise.mapper.NoiseMapper;
import com.dosion.noisense.web.common.dto.ResponseDto;
import com.dosion.noisense.web.noise.dto.request.NoiseStatRequestDto;
import com.dosion.noisense.web.noise.dto.response.DashboardMapNoiseResponseDto;
import com.dosion.noisense.web.noise.dto.response.FullMapNoiseResponseDto;
import com.dosion.noisense.web.noise.dto.response.MainMapNoiseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/noise")
@RequiredArgsConstructor
public class NoiseStatController {

  private final NoiseStatService noiseStatService;

  // ✅ 1. 메인 지도용: 자치구 평균 소음 & 체감 소음
  @GetMapping("/main")
  public ResponseDto<MainMapNoiseResponseDto> getMainMapStats(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
  ) {
    var stats = noiseStatService.getMainMapStats(startDate, endDate);
    return ResponseDto.success(stats);
  }

  // ✅ 2. 대시보드 지도용: 특정 자치구 하위 행정동
  @GetMapping("/dashboard")
  public ResponseDto<DashboardMapNoiseResponseDto> getDashboardMapStats(
    @RequestParam String autonomousCode,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
  ) {
    var stats = noiseStatService.getDashboardMapStats(startDate, endDate, autonomousCode);
    return ResponseDto.success(stats);
  }

  // ✅ 3. 풀맵 지도용: 자치구/행정동 전체 요청 (zoomLevel로 구분 X, 프론트에서 분기)
//  @PostMapping("/full")
//  public ResponseDto<FullMapNoiseResponseDto> getFullMapStats(
//    @RequestBody NoiseStatRequestDto requestDto
//  ) {
//    var autonomousStats = noiseStatService.getFullMapStatsAutonomous(requestDto.startDate(), requestDto.endDate(), requestDto.regionList());
//    var administrativeStats = noiseStatService.getFullMapStatsAdministrative(requestDto.startDate(), requestDto.endDate(), requestDto.regionList());
//
//    return ResponseDto.success(null);
//  }

  @PostMapping("/full/autonomous")
  public ResponseDto<FullMapNoiseResponseDto> getFullMapStatsAutonomous(
    @RequestBody NoiseStatRequestDto requestDto
  ) {
    return ResponseDto.success(
      noiseStatService.getFullMapStatsAutonomous(requestDto.startDate(), requestDto.endDate(), requestDto.regionList())
    );
  }

  @PostMapping("/full/administrative")
  public ResponseDto<FullMapNoiseResponseDto> getFullMapStatsAdministrative(
    @RequestBody NoiseStatRequestDto requestDto
  ) {
    return ResponseDto.success(
      noiseStatService.getFullMapStatsAdministrative(requestDto.startDate(), requestDto.endDate(), requestDto.regionList())
    );
  }
}
