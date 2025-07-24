package com.dosion.noisense.web.district.controller;

import com.dosion.noisense.module.district.service.DistrictService;
import com.dosion.noisense.web.common.dto.ResponseDto;
import com.dosion.noisense.web.district.dto.DistrictDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/district")
@AllArgsConstructor
public class DistrictController {

  private final DistrictService districtService;

  @GetMapping("/all")
  public ResponseEntity<?> getAllDistricts() {

    try {
      List<DistrictDto> districtDtos = districtService.getAllDistricts();
      return ResponseEntity.ok(ResponseDto.success(districtDtos));
    } catch(Exception e) {
      return ResponseEntity.ok(ResponseDto.fail(e.getMessage(), e.getCause().getMessage()));
    }
  }

  @GetMapping("/autonomousDistrict")
  public ResponseEntity<?> getAutonomousDistricts() {

    try {
      List<DistrictDto> districtDtos = districtService.getAllAutonomousDistricts();
      return ResponseEntity.ok(ResponseDto.success(districtDtos));
    } catch(Exception e) {
      return ResponseEntity.ok(ResponseDto.fail(e.getMessage(), e.getCause().getMessage()));
    }
  }

  @GetMapping("/administrativeDistrict")
  public ResponseEntity<?> getAdministrativeDistricts() {

    try {
      List<DistrictDto> districtDtos = districtService.getAllAdministrativeDistricts();
      return ResponseEntity.ok(ResponseDto.success(districtDtos));
    } catch(Exception e) {
      return ResponseEntity.ok(ResponseDto.fail(e.getMessage(), e.getCause().getMessage()));
    }
  }
}
