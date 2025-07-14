package com.dosion.noisense.module.api.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "autonomous_district")
public class AutonomousDistrictEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String code;

  @Column(name = "name_ko")
  private String nameKo;

  @Column(name = "name_en")
  private String nameEn;

}
