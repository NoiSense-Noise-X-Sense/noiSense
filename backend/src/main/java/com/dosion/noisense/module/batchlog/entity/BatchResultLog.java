package com.dosion.noisense.module.batchlog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch_result_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BatchResultLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "batch_result_log_id")
  private Long id;

  @Column(name = "job_name", nullable = false)
  private String jobName;

  @Column(name = "message")
  private String message;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @PrePersist
  protected void onCreate() {
    this.createdDate = LocalDateTime.now();
  }
}
