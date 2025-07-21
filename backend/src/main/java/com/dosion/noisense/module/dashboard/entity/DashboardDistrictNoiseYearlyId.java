package com.dosion.noisense.module.dashboard.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDistrictNoiseYearlyId implements Serializable {
  private String regionType;
  private String regionName;
  private Integer year;
}
