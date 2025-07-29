package com.dosion.noisense.module.dashboard.repository;

import com.dosion.noisense.module.dashboard.entity.NoiseComplaints;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoiseComplaintsRepository extends JpaRepository<NoiseComplaints, Long> {

  List<NoiseComplaints> findByAutonomousDistrictAndYearBetweenOrderByYearAsc(
    String autonomousDistrict, int fromYear, int toYear
  );
}
