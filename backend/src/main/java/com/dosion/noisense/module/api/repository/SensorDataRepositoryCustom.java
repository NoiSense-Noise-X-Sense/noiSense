package com.dosion.noisense.module.api.repository;


import com.dosion.noisense.module.api.entity.SensorData;
import com.dosion.noisense.web.report.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SensorDataRepositoryCustom {


  void bulkInsert(List<SensorData> entityList);

  /*
    해당 지역의 최대 소음 지역
    autonomous : 강남구 강동구 등 행정구, 서울시 전국일 경우 all
   */
  MaxNoiseDto findLoudesDistrict(LocalDate startDate, LocalDate endDate, String autonomousDistrict);

}
