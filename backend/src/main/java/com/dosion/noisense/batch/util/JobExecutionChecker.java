package com.dosion.noisense.batch.util;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobExecutionChecker {

  private final JdbcTemplate jdbcTemplate;

  /**
   * 오늘 이미 같은 Job이 실행된 적이 있는지 확인
   *
   * @param jobName             Job 이름
   * @param currentExecutionId  현재 실행 ID (자기 자신은 제외)
   * @return true = 이미 실행된 적 있음 / false = 오늘 첫 실행
   */
  public boolean hasJobCompletedTodayBefore(String jobName, Long currentExecutionId) {
    String sql = """
            SELECT COUNT(*) FROM batch_job_execution e
            JOIN batch_job_instance i ON e.job_instance_id = i.job_instance_id
            WHERE i.job_name = ?
              AND e.status = 'COMPLETED'
              AND e.create_time::date = CURRENT_DATE
              AND e.job_execution_id < ?
        """;

    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, jobName, currentExecutionId);
    return count != null && count > 0;
  }
}
