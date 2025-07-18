package com.dosion.noisense.module.geodata.repository;

import com.dosion.noisense.module.geodata.entity.BoundaryPolygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoundaryPolygonRepository extends JpaRepository<BoundaryPolygon, String> {

  //  // 검색 조건 별 데이터
  @Query("""
        SELECT bp
    FROM BoundaryPolygon bp
    """)
  List<BoundaryPolygon> findAllPolygons();
}
