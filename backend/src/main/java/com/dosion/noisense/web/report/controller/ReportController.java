package com.dosion.noisense.web.report.controller;


import com.dosion.noisense.module.report.service.ReportService;
import com.dosion.noisense.web.dashboard.dto.RegionNoiseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
//    content = @Content(schema = @Schema(implementation = RegionNoiseDto.class))
  )
  @GetMapping("/test")
  public String getReport(@RequestParam String startDate, @RequestParam String endDate, @RequestParam String autonomousDistrict){
    return reportService.getReport(startDate, endDate, autonomousDistrict);
  }


}
