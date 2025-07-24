package com.dosion.noisense.module.dashboard.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDistrictNoiseHourlyId implements Serializable {
  private String batchId;
  private String autonomousDistrictCode;
  private Integer hour;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DashboardDistrictNoiseHourlyId)) return false;
    DashboardDistrictNoiseHourlyId that = (DashboardDistrictNoiseHourlyId) o;
    return Objects.equals(batchId, that.batchId)
      && Objects.equals(autonomousDistrictCode, that.autonomousDistrictCode)
      && Objects.equals(hour, that.hour);
  }

  @Override
  public int hashCode() {
    return Objects.hash(batchId, autonomousDistrictCode, hour);
  }
}
