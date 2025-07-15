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
  private static final String SERVICE_NAME = "IotVdata017";

  // 전체 데이터 건수 가져오기
  public int getTotalCount() {
    try {
      // .block()을 호출하여 Mono<String>의 결과를 동기적으로 얻어옵니다.
      String responseString = callApiForDistrict(null, 1, 1).block();

      if (responseString != null && !responseString.isEmpty()) {
        JSONObject jsonObject = new JSONObject(responseString);
        // 키가 없는 경우를 대비해 optInt 사용
        return jsonObject.getJSONObject(SERVICE_NAME).optInt("list_total_count", 0);
      }
    } catch (Exception e) {
      log.error("전체 데이터 건수 조회 중 오류 발생", e);
    }
    return 0;
  }

  // WebClient.Builder와 apiKey를 생성자로 주입
  public SensorDataApiReader(WebClient.Builder webClientBuilder, @Value("${api.seoul.key}") String apiKey) {
    this.webClient = webClientBuilder.baseUrl("http://openapi.seoul.go.kr:8088").build();
    this.apiKey = apiKey;
  }

  public Mono<String> callApiForDistrict(String districtNameEn, int startIndex, int endIndex) {

    if(districtNameEn == null || districtNameEn.isEmpty()){
      String path = String.format("/%s/json/%s/%d/%d/", apiKey, SERVICE_NAME, startIndex, endIndex);

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

    } else {
      String path = String.format("/%s/json/%s/%d/%d/%s", apiKey, SERVICE_NAME, startIndex, endIndex, districtNameEn);

      return webClient.get()
        .uri(path)
        .retrieve()
        // API 에러 처리 (4xx, 5xx 등)
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          log.error("API 요청 실패: status={}, districtNameEn={}, startIndex={}, endIndex={}",
            clientResponse.statusCode(), districtNameEn, startIndex, endIndex);
          return clientResponse.bodyToMono(String.class)
            .flatMap(errorBody -> Mono.error(new RuntimeException("API Call Failed: " + errorBody)));
        })
        .bodyToMono(String.class);
    }
  }
}
