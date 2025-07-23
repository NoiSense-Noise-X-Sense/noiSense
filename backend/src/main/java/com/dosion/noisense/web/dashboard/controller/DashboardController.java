package com.dosion.noisense.web.dashboard.controller; // 패키지는 프로젝트 구조에 맞게 생성하세요.

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  // 임시로 빈 데이터를 정상 응답으로 보내는 메서드들
  @GetMapping("/summary")
  public ResponseEntity<?> getSummary() {
    return ResponseEntity.ok(Collections.emptyMap());
  }

  @GetMapping("/complaints")
  public ResponseEntity<?> getComplaints() {
    return ResponseEntity.ok(Collections.emptyList());
  }

  @GetMapping("/yearly")
  public ResponseEntity<?> getYearlyData() {
    return ResponseEntity.ok(Collections.emptyMap());
  }

  @GetMapping("/hourly")
  public ResponseEntity<?> getHourlyData() {
    return ResponseEntity.ok(Collections.emptyMap());
  }
}
