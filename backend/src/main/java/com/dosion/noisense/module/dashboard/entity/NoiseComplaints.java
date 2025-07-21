package com.dosion.noisense.module.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "noise_complaints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoiseComplaints {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "noise_complaints_id")
  private Long id;

  @Column(name = "autonomous_district", nullable = false)
  private String autonomousDistrict;

  @Column(name = "year", nullable = false)
  private Integer year;

  @Column(name = "count", nullable = false)
  private Integer count;
}
