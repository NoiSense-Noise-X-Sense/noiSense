package com.dosion.noisense.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false) // 원하는 JsonNode 필드에만 적용하기 위함
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(JsonNode attribute) {
    if (attribute == null) return null;

    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to convert JsonNode to String", e);
    }
  }

  @Override
  public JsonNode convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) return null;

    try {
      return objectMapper.readTree(dbData);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to convert String to JsonNode", e);
    }
  }
}
