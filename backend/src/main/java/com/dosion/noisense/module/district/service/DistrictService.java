package com.dosion.noisense.module.district.service;

import com.dosion.noisense.module.district.entity.District;
import com.dosion.noisense.module.district.repository.DistrictRepository;
import com.dosion.noisense.web.district.dto.DistrictDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class DistrictService {

  private final DistrictRepository districtRepository;

  public List<DistrictDto> getAllDistricts() {

//    List<District> all = districtRepository.findAllDistricts();
    List<District> autonomous = districtRepository.findAllAutonomousDistricts();
    List<District> administrative = districtRepository.findAllAdministrativeDistricts();

    List<DistrictDto> districtDtos = Stream.concat(autonomous.stream(), administrative.stream())
      .map(District::toDto)
      .collect(Collectors.toList());

    log.info("distirctDtos : {}", districtDtos.size());

    return districtDtos;
  }


}
