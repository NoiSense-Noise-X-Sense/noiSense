package com.dosion.noisense.module.dashboard.entity;

import java.io.Serializable;
import java.util.Objects;

public class DashboardDistrictNoiseSummaryId implements Serializable {
  private String batchId;
  private String autonomousDistrictCode;

  public DashboardDistrictNoiseSummaryId() {}

  public DashboardDistrictNoiseSummaryId(String batchId, String autonomousDistrict) {
    this.batchId = batchId;
    this.autonomousDistrictCode = autonomousDistrict;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DashboardDistrictNoiseSummaryId)) return false;
    DashboardDistrictNoiseSummaryId that = (DashboardDistrictNoiseSummaryId) o;
    return Objects.equals(batchId, that.batchId)
      && Objects.equals(autonomousDistrictCode, that.autonomousDistrictCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(batchId, autonomousDistrictCode);
  }
}
