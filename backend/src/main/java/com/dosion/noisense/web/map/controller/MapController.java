package com.dosion.noisense.web.map.controller;

import com.dosion.noisense.module.map.service.MapService;
import com.dosion.noisense.web.map.dto.MapDto;
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

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Noise-Map-Controller", description = "소음 지도")
@RestController
@AllArgsConstructor
@RequestMapping("/api/map")
public class MapController {

  private final MapService mapService;

  @Operation(summary = "지도 호출", description = "기간별, 지역별")
  @ApiResponse(
    responseCode = "200",
    description = "성공",
    content = @Content(schema = @Schema(implementation = MapDto.class))
  )
  @GetMapping("/getMap")
  public ResponseEntity<List<MapDto>> getMap(
    @RequestParam @Parameter(example = "2024-06-01 00:00") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime startDate,
    @RequestParam @Parameter(example = "2025-07-30 23:59") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime endDate,
    @RequestParam @Parameter(example = "11020") String autonomousDistrictKor,
    @RequestParam @Parameter(example = "all") String administrativeDistrictKor,
    @RequestParam @Parameter(example = "") List<String> regionList) {
    return ResponseEntity.ok(mapService.getMapData(startDate, endDate, autonomousDistrictKor, administrativeDistrictKor, regionList));
  }
}
