package com.dosion.noisense.module.api.repository;


import com.dosion.noisense.module.api.entity.SensorDataApiEntity;

import java.util.List;

public interface SensorDataRepositoryCustom {

  void bulkInsert(List<SensorDataApiEntity> entityList);

}
