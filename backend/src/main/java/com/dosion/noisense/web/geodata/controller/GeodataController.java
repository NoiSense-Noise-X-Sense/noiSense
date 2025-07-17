package com.dosion.noisense.web.geodata.controller;

import com.dosion.noisense.module.geodata.service.GeodataService;
import com.dosion.noisense.web.common.dto.ResponseDto;
import com.dosion.noisense.web.geodata.dto.BoundaryPolygonDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/geodata")
@AllArgsConstructor
public class GeodataController {

  private final GeodataService geodataService;

  /*자치구 폴리곤 조회: 무조건 모든 경계 그려줘야 함*/
  @PostMapping("/polygon/all")
  public ResponseEntity<?> getAllPolygons() {

    try {
      List<BoundaryPolygonDto> polygonDtos = geodataService.getAllPolygons();
      return ResponseEntity.ok(ResponseDto.success(polygonDtos));
    } catch (Exception e) {
      return ResponseEntity.ok(ResponseDto.fail(e.getMessage(), e.getCause().getMessage()));
    }
  }
}
