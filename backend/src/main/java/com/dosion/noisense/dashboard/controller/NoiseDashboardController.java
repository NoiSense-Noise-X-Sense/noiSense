package com.dosion.noisense.dashboard.controller;

import com.dosion.noisense.dashboard.dto.RegionNoiseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Noise-Dashboard-Controller", description = "지역별 소음 대시보드")
@RestController
@RequestMapping("/api/dashboard")
public class NoiseDashboardController {

  @Operation(summary = "지역별 소음 통계 호출", description = "연도별, 시간별")
  @ApiResponse(
      responseCode = "200",
      description = "성공",
      content = @Content(schema = @Schema(implementation = RegionNoiseDto.class))
  )
  @GetMapping("/region")
  public ResponseEntity<List<RegionNoiseDto>> getNoiseByRegion(@RequestParam String region) {
//    return ResponseEntity.ok(noiseService.getNoiseByRegion());
    return null;
  }
}
