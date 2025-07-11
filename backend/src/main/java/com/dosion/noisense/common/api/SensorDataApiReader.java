package com.dosion.noisense.common.api;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class SensorDataApiReader {

  @Value("${api.seoul.key}")
  private String apiKey;

  private static final String SERVICE_NAME = "IotVdata017";
  private static final int MAX_RESULTS_PER_PAGE = 1000; // API 최대 요청 건수

  // 전체 데이터 건수 가져오기
  public int getTotalCount() throws Exception {

    // 1건만 요청하여 전체 개수 파악
    String response = callApi(1, 1);

    if (response != null && !response.isEmpty()) {

      JSONObject jsonObject = new JSONObject(response);
      return jsonObject.getJSONObject(SERVICE_NAME).getInt("list_total_count");

    }
    return 0;
  }

  // 지정된 범위 데이터 불러오기
  public String callApi(int startIndex, int endIndex) throws Exception {
    String requestUrl = String.format("http://openapi.seoul.go.kr:8088/%s/json/%s/%d/%d/",
      apiKey, SERVICE_NAME, startIndex, endIndex);

    URL url = new URL(requestUrl);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");

    // 타임아웃 5초 두기
    conn.setConnectTimeout(30000);
    conn.setReadTimeout(30000);

    StringBuilder response = new StringBuilder();
    int responseCode = conn.getResponseCode();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(

      responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(), "UTF-8"))) {
      String line;

      while ((line = br.readLine()) != null) {
        response.append(line);
      }

    } finally {
      conn.disconnect();
    }

    if (responseCode >= 200 && responseCode < 300) {

      return response.toString();

    } else {

      System.err.printf("API 요청 실패: (상태 코드: %d, 시작: %d, 종료: %d)%n응답: %s%n",
        responseCode, startIndex, endIndex, response);

      return null;
    }
  }
}
