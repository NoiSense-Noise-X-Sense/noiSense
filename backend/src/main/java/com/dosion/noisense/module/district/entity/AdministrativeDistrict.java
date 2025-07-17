package com.dosion.noisense.module.district.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administrative_district", schema = "noisense")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdministrativeDistrict {

  @Id
  @Column(name = "code", length = 10, nullable = false)
  private String code;

  @Column(name = "name_ko", length = 50, nullable = false)
  private String nameKo;

  @Column(name = "name_en", length = 50, nullable = false)
  private String nameEn;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "autonomous_district", nullable = false)
  private AutonomousDistrict autonomousDistrict;
}
