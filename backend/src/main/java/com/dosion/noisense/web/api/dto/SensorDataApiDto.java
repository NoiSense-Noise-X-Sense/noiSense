package com.dosion.noisense.web.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataApiDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd_HH:mm:ss")
  @JsonProperty("SENSING_TIME")
  private LocalDateTime sensingTime;

  // 지역 데이터
  @JsonProperty("REGION")
  private String region;

  @JsonProperty("AUTONOMOUS_DISTRICT")
  private String autonomousDistrict;

  @JsonProperty("ADMINISTRATIVE_DISTRICT")
  private String administrativeDistrict;

  // 소음 데이터
  @JsonProperty("MAX_NOISE")
  private Double maxNoise;

  @JsonProperty("AVG_NOISE")
  private Double avgNoise;

  @JsonProperty("MIN_NOISE")
  private Double minNoise;

  // 온도 데이터
  @JsonProperty("MAX_TEMP")
  private Double maxTemp;

  @JsonProperty("AVG_TEMP")
  private Double avgTemp;

  @JsonProperty("MIN_TEMP")
  private Double minTemp;

  // 습도 데이터
  @JsonProperty("MAX_HUMI")
  private Double maxHumi;

  @JsonProperty("AVG_HUMI")
  private Double avgHumi;

  @JsonProperty("MIN_HUMI")
  private Double minHumi;

}
