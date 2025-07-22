package com.dosion.noisense.module.report.service;

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
  private  Map<String, String> CODE_TO_ENG = new HashMap<>();
  private  Map<String, String> ENG_TO_CODE = new HashMap<>();

  public  String toCode(String eng) {
    return ENG_TO_CODE.getOrDefault(eng, eng);
  }

  public  String toENG(String code) {
    return CODE_TO_ENG.getOrDefault(code, code);
  }

  // JSON 파일을 읽어서 담는 과정
  @PostConstruct
  public void init() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ClassPathResource resource = new ClassPathResource("engCode.json");
    InputStream inputStream = resource.getInputStream();

    List<RegionMapping> regionMappingList = mapper.readValue(inputStream, new TypeReference<>() {
    });

    CODE_TO_ENG = regionMappingList.stream().collect(Collectors.toMap(RegionMapping::getDistrictCode, RegionMapping::getDistrictEnglish, (oldValue, newValue) -> newValue));
    ENG_TO_CODE = regionMappingList.stream().collect(Collectors.toMap(RegionMapping::getDistrictEnglish, RegionMapping::getDistrictCode, (oldValue, newValue) -> newValue));
  }

  // JSON 파일의 각 객체를 담는 클래스
  @Getter
  @Setter
  private static class RegionMapping {
    private String districtCode;
    private String districtEnglish;
  }

}
