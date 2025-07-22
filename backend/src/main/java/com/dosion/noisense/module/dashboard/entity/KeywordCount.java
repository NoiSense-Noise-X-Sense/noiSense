package com.dosion.noisense.module.dashboard.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter, setter, equals, hashCode, toString 모두 포함
@NoArgsConstructor
@AllArgsConstructor
public class KeywordCount {
  private String keyword;
  private int count;
}
