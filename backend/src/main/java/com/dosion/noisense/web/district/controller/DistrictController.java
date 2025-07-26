package com.dosion.noisense.web.district.controller;

import com.dosion.noisense.module.district.service.DistrictService;
import com.dosion.noisense.web.common.dto.ResponseDto;
import com.dosion.noisense.web.district.dto.DistrictDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
      return ResponseEntity.ok(ResponseDto.fail(e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "Unknown error"));
    }
  }

  @GetMapping("/autonomousDistrict")
  public ResponseEntity<?> getAutonomousDistricts() {

    try {
      List<DistrictDto> districtDtos = districtService.getAllAutonomousDistricts();
      return ResponseEntity.ok(ResponseDto.success(districtDtos));
    } catch(Exception e) {
      return ResponseEntity.ok(ResponseDto.fail(e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "Unknown error"));
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

  @Operation(summary = "구 코드 하위의 모든 동 데이터 호출",
    description = "gu (district) code 하위의 모든 동 데이터를 가져옵니다."
  )
  @GetMapping("/gu/{guCode}/dongs")
  public ResponseEntity<?> getDongByGu(@PathVariable String guCode) {

    try {
      List<DistrictDto> districtDtos = districtService.getDongByGu(guCode);
      return ResponseEntity.ok(ResponseDto.success(districtDtos));
    } catch (Exception e) {
      return ResponseEntity.ok(ResponseDto.fail(e.getMessage(), e.getCause().getMessage()));
    }
  }
}
