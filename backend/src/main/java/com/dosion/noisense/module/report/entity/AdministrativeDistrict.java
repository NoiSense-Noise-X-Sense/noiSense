package com.dosion.noisense.module.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "administrative_district", schema = "noisense")
public class AdministrativeDistrict {

  @Id
  @Column(name = "code")
  private String code;

  @Column(name = "name_ko")
  private String nameKo;

  @Column(name = "name_en")
  private String nameEn;

  @Column(name = "autonomous_district")
  private String autonomousDistrict;

}
