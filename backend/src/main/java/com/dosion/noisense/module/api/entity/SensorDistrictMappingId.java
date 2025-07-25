package com.dosion.noisense.module.api.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.*;

@Embeddable // 복합 키 클래스임을 명시
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 복합 키 비교를 위해 필수
public class SensorDistrictMappingId implements Serializable {

  private String sensorAutoDistrictEn;
  private String sensorAdminDistrictEn;
}
