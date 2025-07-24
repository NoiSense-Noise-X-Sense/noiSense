package com.dosion.noisense.module.dashboard.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDistrictNoiseZoneId implements Serializable {
  private String batchId;
  private String autonomousDistrictCode;
  private String administrativeDistrictCode;
}
