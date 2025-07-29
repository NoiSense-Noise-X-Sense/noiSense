package com.dosion.noisense.common.converter; // 패키지 경로는 프로젝트에 맞게 생성하세요.

import com.dosion.noisense.module.sensor.enums.Region; // Region Enum import
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// autoApply=true를 설정하면 이 컨버터를 사용하는 모든 Region 타입에 자동으로 적용됩니다.
@Converter(autoApply = true)
public class RegionConverter implements AttributeConverter<Region, String> {

  @Override
  public String convertToDatabaseColumn(Region attribute) {
    if (attribute == null) {
      return null;
    }
    // Enum 객체를 DB에 저장할 때는 nameEn 필드 값(소문자)을 사용합니다.
    // 예: Region.TRADITIONAL_MARKETS -> "traditional_markets"
    return attribute.getNameEn();
  }

  @Override
  public Region convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.trim().isEmpty()) {
      return null;
    }
    // DB에서 읽어온 문자열(소문자)로 Enum 객체를 찾습니다.
    // 예: "traditional_markets" -> Region.TRADITIONAL_MARKETS
    return Region.fromNameEn(dbData);
  }
}
