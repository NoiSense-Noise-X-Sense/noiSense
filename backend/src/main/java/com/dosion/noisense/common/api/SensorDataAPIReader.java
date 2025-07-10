package com.dosion.noisense.common.api;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.dosion.noisense.common.dto.SensorDataDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;


@Component
public class SensorDataAPIReader implements ItemReader<SensorDataDTO> {

  @Override
  public SensorDataDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {


    return null;
  }

  public static void main(String[] args) {

    // 1. 요청을 위한 변수 설정
    String apiKey = "777050586b776b64353266596d5643";
    String serviceName = "IotVdata017"; // 명세서에 명시된 정확한 서비스명
    int startIndex = 1;
    int endIndex = 100; // 테스트를 위해 5건만 요청

    // 요청 URL 구성
    String requestUrl = String.format("http://openapi.seoul.go.kr:8088/%s/json/%s/%d/%d/",
      apiKey, serviceName, startIndex, endIndex);

    HttpURLConnection conn = null;
    StringBuilder response = new StringBuilder();

    try {
      // 2. HTTP 요청 보내기
      URL url = new URL(requestUrl);
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Content-type", "application/json");

      // 3. 응답 상태 확인 및 데이터 읽기
      int responseCode = conn.getResponseCode();
      if (responseCode >= 200 && responseCode < 300) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
          String line;
          while ((line = br.readLine()) != null) {
            response.append(line);
          }
        }
      } else {
        System.out.println("Error: API 요청에 실패했습니다. (상태 코드: " + responseCode + ")");
        // 실패 시 응답 내용 확인
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"))) {
          String line;
          while ((line = br.readLine()) != null) {
            System.out.println(line);
          }
        }
        return;
      }

      // 4. JSON 데이터 파싱 및 활용
      JSONObject jsonObject = new JSONObject(response.toString());
      // 명세서에 따라 응답의 최상위 객체 이름은 서비스명과 동일
      JSONObject sdotData = jsonObject.getJSONObject(serviceName);
      JSONArray rowData = sdotData.getJSONArray("row");

      System.out.println("S-DoT 환경 정보 (총 " + sdotData.getInt("list_total_count") + "건 중 " + rowData.length() + "건)");
      System.out.println("----------------------------------------------------------------------------------");

      for (int i = 0; i < rowData.length(); i++) {
        JSONObject item = rowData.getJSONObject(i);

        // API 명세서의 출력값(XML 예시의 태그명)을 기반으로 키 값 사용
        String sensingTime = item.optString("SENSING_TIME");
        String serialNo = item.optString("SERIAL_NO");
        double maxNoise = item.optDouble("MAX_NOISE");
        double avgNoise = item.optDouble("AVG_NOISE");
        double minNoise = item.optDouble("MIN_NOISE");
        double maxTemp = item.optDouble("MAX_TEMP");
        double avgTemp = item.optDouble("AVG_TEMP");
        double minTemp = item.optDouble("MIN_TEMP");
        double maxHumi = item.optDouble("MAX_HUMI");
        double avgHumi = item.optDouble("AVG_HUMI");
        double minHumi = item.optDouble("MIN_HUMI");



        System.out.printf("센서 번호: %s | 측정시간: %s%n", serialNo, sensingTime);
        System.out.printf("최대소음: %.1fdB | 평균소음: %.1fdB | 최소소음: %.1fdB%n", maxNoise, avgNoise, minNoise);
        System.out.printf("최대온도: %.1f°C | 평균온도: %.1f°C | 최소온도: %.1f°C%n", maxTemp, avgTemp, minTemp);
        System.out.printf("최대습도: %.1f%% | 평균습도: %.1f%% | 최소습도: %.1f%%%n", maxHumi, avgHumi, minHumi);

      }

    } catch (Exception e) {
      System.err.println("데이터 처리 중 오류가 발생했습니다.");
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }


}
