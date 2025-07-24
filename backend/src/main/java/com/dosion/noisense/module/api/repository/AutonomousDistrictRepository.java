package com.dosion.noisense.module.api.repository;

import com.dosion.noisense.module.api.entity.AutonomousDistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AutonomousDistrictRepository extends JpaRepository<AutonomousDistrictEntity, String> {

  List<AutonomousDistrictEntity> findAllByOrderByNameKoAsc();

}
