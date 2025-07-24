package com.dosion.noisense.module.api.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sensor_data")
public class SensorDataApiEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sensor_data_id")
  private Long id;

  @Column(name = "sensing_time", nullable = false)
  private LocalDateTime sensingTime;

  // 지역 데이터
  @Column(name = "region", columnDefinition = "region_type" )
  private Region region;

  // 자치구
  @Column(name = "autonomous_district", nullable = false)
  private String autonomousDistrict;

  // 행정동
  @Column(name = "administrative_district", nullable = false)
  private String administrativeDistrict;

  // 소음 데이터
  @Column(name = "max_noise")
  private Double maxNoise;

  @Column(name = "avg_noise")
  private Double avgNoise;

  @Column(name = "min_noise")
  private Double minNoise;

  // 온도 데이터
  @Column(name = "max_temp")
  private Double maxTemp;

  @Column(name = "avg_temp")
  private Double avgTemp;

  @Column(name = "min_temp")
  private Double minTemp;

  // 습도 데이터
  @Column(name = "max_humi")
  private Double maxHumi;

  @Column(name = "avg_humi")
  private Double avgHumi;

  @Column(name = "min_humi")
  private Double minHumi;

  // 배치 시간
  @Column(name = "batch_time")
  private LocalDateTime batchTime;

  @Builder
  public SensorDataApiEntity(LocalDateTime sensingTime, Region region , String autonomousDistrict, String administrativeDistrict,
                             Double maxNoise, Double avgNoise, Double minNoise, Double maxTemp, Double avgTemp,
                             Double minTemp, Double maxHumi, Double avgHumi, Double minHumi) {
    this.sensingTime = sensingTime;
    this.region = region;
    this.autonomousDistrict = autonomousDistrict;
    this.administrativeDistrict = administrativeDistrict;
    this.maxNoise = maxNoise;
    this.avgNoise = avgNoise;
    this.minNoise = minNoise;
    this.maxTemp = maxTemp;
    this.avgTemp = avgTemp;
    this.minTemp = minTemp;
    this.maxHumi = maxHumi;
    this.avgHumi = avgHumi;
    this.minHumi = minHumi;
    this.batchTime = LocalDateTime.now();

  }

}


