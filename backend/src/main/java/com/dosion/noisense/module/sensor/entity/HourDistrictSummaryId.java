package com.dosion.noisense.module.sensor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HourDistrictSummaryId implements Serializable {

  @Column(name = "sensing_hour")
  private LocalDateTime sensingHour;

  @Column(name = "administrative_code")
  private String administrativeCode;

}
