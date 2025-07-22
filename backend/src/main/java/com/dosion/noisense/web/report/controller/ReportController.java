package com.dosion.noisense.web.report.controller;

import com.dosion.noisense.module.report.service.ReportService;
import com.dosion.noisense.web.report.dto.ReportDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam; // ✅ @RequestParam import 추가

import java.time.LocalDate;

@Tag(name = "Noise-Report-Controller", description = "소음 리포트")
@RestController
@AllArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

  private final ReportService reportService;

  @Operation(summary = "리포트 호출", description = "기간별, 지역별")
  @ApiResponse(
    responseCode = "200",
    description = "성공"
  )
  @GetMapping("/getReport")
  public ResponseEntity<ReportDto> getReport(
    // ✅ 모든 파라미터 앞에 @RequestParam 을 붙여줍니다.
    @Parameter(example = "2024-06-01") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
    @Parameter(example = "2025-07-30") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
    @Parameter(example = "전체") @RequestParam String autonomousDistrict
  ) {
    return ResponseEntity.ok(reportService.getReport(startDate, endDate, autonomousDistrict));
  }

}
