package com.dosion.noisense.module.report;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RegionConverter {

  // 한글과 영어를 키와 밸류로 각각 담아 변환할때 사용하는 map
  private static Map<String, String> KOR_TO_ENG = new HashMap<>();
  private static Map<String, String> ENG_TO_KOR = new HashMap<>();

  public static String toEnglish(String koreanName) {
    return KOR_TO_ENG.getOrDefault(koreanName, koreanName);
  }

  public static String toKorean(String englishName) {
    return ENG_TO_KOR.getOrDefault(englishName, englishName);
  }

  // JSON 파일을 읽어서 담는 과정
  @PostConstruct
  public void init() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ClassPathResource resource = new ClassPathResource("regionNameKoreanEnglish.json");
    InputStream inputStream = resource.getInputStream();

    List<RegionMapping> regionMappingList = mapper.readValue(inputStream, new TypeReference<>() {
    });

    KOR_TO_ENG = regionMappingList.stream().collect(Collectors.toMap(RegionMapping::getKorean_name, RegionMapping::getEnglish_name));
    ENG_TO_KOR = regionMappingList.stream().collect(Collectors.toMap(RegionMapping::getEnglish_name, RegionMapping::getKorean_name));
  }

  // JSON 파일의 각 객체를 담는 클래스
  @Getter
  @Setter
  private static class RegionMapping {
    private String korean_name;
    private String english_name;
  }

}
