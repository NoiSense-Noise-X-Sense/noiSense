package com.dosion.noisense.web.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SensorDataApiReader {

  private final WebClient webClient;
  private final String apiKey;
  private @Value("${api.seoul.service-name}")String serviceName;

  // 전체 데이터 건수 가져오기
  public int getTotalCount() {
    try {
      // .block()을 호출하여 Mono<String>의 결과를 동기적으로 얻어옵니다.
      String responseString = callApiForDistrict(null, 1, 1).block();

      if (responseString != null && !responseString.isEmpty()) {
        JSONObject jsonObject = new JSONObject(responseString);
        // 키가 없는 경우를 대비해 optInt 사용
        return jsonObject.getJSONObject(serviceName).optInt("list_total_count", 0);
      }
    } catch (Exception e) {
      log.error("전체 데이터 건수 조회 중 오류 발생", e);
    }
    return 0;
  }

  // WebClient.Builder와 apiKey를 생성자로 주입
  public SensorDataApiReader(WebClient.Builder webClientBuilder, @Value("${api.seoul.key}") String apiKey, @Value("${api.seoul.uri}") String apiUri) {
    this.webClient = webClientBuilder.baseUrl(apiUri).build();
    this.apiKey = apiKey;
  }

  public Mono<String> callApiForDistrict(String districtNameEn, int startIndex, int endIndex) {

    String path;
    // SENSING_TIME을 기준으로 내림차순(최신순) 정렬 파라미터를 추가합니다.
    String sortOrder = "?$order=SENSING_TIME DESC";

    if (districtNameEn == null || districtNameEn.isEmpty()) {
      // 최초 데이터 넣을 때는 정렬 X -> 오래 걸림
      path = String.format("/%s/json/%s/%d/%d", apiKey, serviceName, startIndex, endIndex);
    } else {
      // 자치구 필터 뒤에 정렬 파라미터 추가
      // sortOrder -> 내장 정렬 파라미터
      path = String.format("/%s/json/%s/%d/%d/%s%s", apiKey, serviceName, startIndex, endIndex, districtNameEn, sortOrder);
    }

    return webClient.get()
      .uri(path)
      .retrieve()
      // API 에러 처리 (4xx, 5xx 등)
      .onStatus(HttpStatusCode::isError, clientResponse -> {
        log.error("API 요청 실패: status={}, startIndex={}, endIndex={}",
          clientResponse.statusCode(), startIndex, endIndex);
        return clientResponse.bodyToMono(String.class)
          .flatMap(errorBody -> Mono.error(new RuntimeException("API Call Failed: " + errorBody)));
      })
      .bodyToMono(String.class);

  }
}
