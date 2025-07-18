package com.dosion.noisense.batch.decider;

import com.dosion.noisense.batch.util.JobExecutionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardTriggerDecider implements JobExecutionDecider {

  private final JobExecutionChecker checker;

  @Override
  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
    // 오늘 날짜 기준, 이미 hourlyNoiseJob이 실행된 적 있는지 확인
    boolean isFirstToday = !checker.hasJobCompletedTodayBefore("hourlyNoiseJob", jobExecution.getId());

    log.info("🧠 오늘 첫 실행인가? {}", isFirstToday);

    if (isFirstToday) {
      return new FlowExecutionStatus("EXECUTE_DASHBOARD"); // 통계 Step 실행
    } else {
      return new FlowExecutionStatus("SKIP_DASHBOARD");    // 통계 생략하고 Job 종료
    }
  }
}
