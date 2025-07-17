package com.dosion.noisense.module.district.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Table(name = "autonomous_district", schema = "noisense")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AutonomousDistrict {

  @Id
  @Column(name = "code", length = 10, nullable = false)
  private String code;

  @Column(name = "name_ko", length = 50, nullable = false)
  private String nameKo;

  @Column(name = "name_en", length = 50, nullable = false)
  private String nameEn;
}
