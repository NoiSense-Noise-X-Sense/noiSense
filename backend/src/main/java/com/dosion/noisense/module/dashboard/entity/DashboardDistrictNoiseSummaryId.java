package com.dosion.noisense.module.dashboard.entity;

import java.io.Serializable;
import java.util.Objects;

public class DashboardDistrictNoiseSummaryId implements Serializable {
  private String batchId;
  private String autonomousDistrict;

  public DashboardDistrictNoiseSummaryId() {}

  public DashboardDistrictNoiseSummaryId(String batchId, String autonomousDistrict) {
    this.batchId = batchId;
    this.autonomousDistrict = autonomousDistrict;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DashboardDistrictNoiseSummaryId)) return false;
    DashboardDistrictNoiseSummaryId that = (DashboardDistrictNoiseSummaryId) o;
    return Objects.equals(batchId, that.batchId)
      && Objects.equals(autonomousDistrict, that.autonomousDistrict);
  }

  @Override
  public int hashCode() {
    return Objects.hash(batchId, autonomousDistrict);
  }
}
