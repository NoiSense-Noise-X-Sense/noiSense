package com.dosion.noisense.common.repository;

import com.dosion.noisense.common.entity.SensorDataApiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorDataApiEntity, Long> {

}
