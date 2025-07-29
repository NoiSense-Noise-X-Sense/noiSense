package com.dosion.noisense.module.sensor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DailyDistrictSummaryId implements Serializable {

  @Column(name = "summary_date")
  private LocalDate summaryDate;

  @Column(name = "administrative_code")
  private String administrativeCode;

}
