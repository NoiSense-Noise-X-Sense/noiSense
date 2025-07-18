package com.dosion.noisense.module.batchlog.repository;

import com.dosion.noisense.module.batchlog.entity.BatchResultLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchResultLogRepository extends JpaRepository<BatchResultLog, Long> {
}
