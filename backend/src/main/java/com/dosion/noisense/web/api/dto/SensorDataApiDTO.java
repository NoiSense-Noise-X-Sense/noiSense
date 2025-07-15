package com.dosion.noisense.web.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataApiDTO {

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


  // 중복 데이터 계산을 위한 카운트
  private int count = 1;

  // 중복 데이터 평균 계산을 위한 메서드
  private void updateFieldAverage(Function<SensorDataApiDTO, Double> getter,
                                  BiConsumer<SensorDataApiDTO, Double> setter,
                                  SensorDataApiDTO other) {

    // 현재 DTO랑 새 DTO
    Double currentAverageObj = getter.apply(this);
    Double newValueObj = getter.apply(other);


    double currentAverage = (currentAverageObj != null) ? currentAverageObj : 0.0;

    double newValue = (newValueObj != null) ? newValueObj : currentAverage;


    // 현재까지의 총합을 계산
    double currentSum = currentAverage * this.count;

    // 새로운 평균을 계산
    double newAverage = (currentSum + newValue) / (this.count + 1);

    // 계산된 새로운 평균 저장
    setter.accept(this, newAverage);


  }

  // 중복 데이터 계산 후 저장
  public void updateAverages(SensorDataApiDTO other) {
    // updateFieldAverage 메서드 사용해 계산
    updateFieldAverage(SensorDataApiDTO::getMaxNoise, SensorDataApiDTO::setMaxNoise, other);
    updateFieldAverage(SensorDataApiDTO::getAvgNoise, SensorDataApiDTO::setAvgNoise, other);
    updateFieldAverage(SensorDataApiDTO::getMinNoise, SensorDataApiDTO::setMinNoise, other);

    updateFieldAverage(SensorDataApiDTO::getMaxTemp, SensorDataApiDTO::setMaxTemp, other);
    updateFieldAverage(SensorDataApiDTO::getAvgTemp, SensorDataApiDTO::setAvgTemp, other);
    updateFieldAverage(SensorDataApiDTO::getMinTemp, SensorDataApiDTO::setMinTemp, other);

    updateFieldAverage(SensorDataApiDTO::getMaxHumi, SensorDataApiDTO::setMaxHumi, other);
    updateFieldAverage(SensorDataApiDTO::getAvgHumi, SensorDataApiDTO::setAvgHumi, other);
    updateFieldAverage(SensorDataApiDTO::getMinHumi, SensorDataApiDTO::setMinHumi, other);

    // 계산 끝나면 카운트
    this.count++;
  }


}
