package com.dosion.noisense.module.api.repository;


import com.dosion.noisense.module.api.entity.SensorDataApiEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface SensorDataRepositoryCustom {

  void bulkInsert(List<SensorDataApiEntity> entityList);

  Set<String> findExistingKeys(LocalDateTime minTime, LocalDateTime maxTime);


}
