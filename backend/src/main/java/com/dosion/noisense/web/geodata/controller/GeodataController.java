package com.dosion.noisense.web.geodata.controller;

import com.dosion.noisense.module.geodata.service.GeodataService;
import com.dosion.noisense.web.common.dto.ResponseDto;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/geodata")
@AllArgsConstructor
public class GeodataController {

  private final GeodataService geodataService;

  /*자치구 폴리곤 조회*/
  @GetMapping("/polygon/autonomousDistrict")
  public ResponseEntity<?> getAutonomousPolygons() {

    try {
      List<BoundaryPolygonDto> polygonDtos = geodataService.getAutonomousPolygons();
      return ResponseEntity.ok(ResponseDto.success(polygonDtos));
    } catch (Exception e) {
      return ResponseEntity.ok(ResponseDto.fail(e.getMessage(), e.getCause().getMessage()));
    }
  }

  /*자치구 폴리곤 조회*/
  @GetMapping("/polygon/administrativeDistrict")
  public ResponseEntity<?> getAdministrativePolygons() {

    try {
      List<BoundaryPolygonDto> polygonDtos = geodataService.getAdministrativePolygons();
      return ResponseEntity.ok(ResponseDto.success(polygonDtos));
    } catch (Exception e) {
      return ResponseEntity.ok(ResponseDto.fail(e.getMessage(), e.getCause().getMessage()));
    }
  }
}
