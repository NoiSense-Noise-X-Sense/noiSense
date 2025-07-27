package com.dosion.noisense.web.report.controller;


import com.dosion.noisense.module.report.service.ReportService;
import com.dosion.noisense.web.report.dto.ReportDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Noise-Report-Controller", description = "소음 리포트")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {

  private final ReportService reportService;

  @Operation(summary = "리포트 호출", description = "기간별, 지역별")
  @ApiResponse(
    responseCode = "200",
    description = "성공",
    content = @Content(schema = @Schema(implementation = ReportDto.class))
  )
  @GetMapping("/getReport") // @Parameter example : swagger 기본값 설정
  public ResponseEntity<ReportDto> getReport(
    @RequestParam @Parameter(example = "2024-06-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate
    , @RequestParam @Parameter(example = "2025-07-30") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    , @RequestParam(required = false, defaultValue = "all") @Parameter(example = "11020") String autonomousDistrictCode) {
    return ResponseEntity.ok(reportService.getReport(startDate, endDate, autonomousDistrictCode));
    // autonomousDistrictCode
  }

}
