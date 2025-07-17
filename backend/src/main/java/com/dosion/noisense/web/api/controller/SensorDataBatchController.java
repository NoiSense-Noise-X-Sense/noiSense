package com.dosion.noisense.web.api.controller;

import com.dosion.noisense.module.api.service.SensorDataApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class SensorDataBatchController {

  private final SensorDataApiService sensorDataApiService;

  //최초로 불러올 때만 사용할 api
  @GetMapping("/run-initial-load")
  public ResponseEntity<String> runInitialLoad() {

    // @Async 덕분에 이 메서드는 즉시 리턴되고, 작업은 백그라운드에서 실행됩니다.
    sensorDataApiService.fetchAllHistoricalData();

    return ResponseEntity.ok("전체 데이터 수집 작업이 시작되었습니다. 서버 로그를 확인해주세요.");
  }

}
