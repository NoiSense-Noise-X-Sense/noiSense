package com.dosion.noisense.module.dashboard.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
@Converter
public class KeywordListConverter implements AttributeConverter<List<KeywordCount>, String> {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<KeywordCount> attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      log.error("Failed to convert KeywordCount list to JSON string: {}", attribute, e);
      return "[]";
    }
  }

  @Override
  public List<KeywordCount> convertToEntityAttribute(String dbData) {
    try {
      if (dbData == null || dbData.isEmpty()) return Collections.emptyList();
      return objectMapper.readValue(dbData, new TypeReference<List<KeywordCount>>() {});
    } catch (Exception e) {
      log.error("Failed to convert JSON string to KeywordCount list: {}", dbData, e);
      return Collections.emptyList();
    }
  }
}
