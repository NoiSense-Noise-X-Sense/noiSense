package com.dosion.noisense.batch.tasklet;

import com.dosion.noisense.module.batchlog.entity.BatchResultLog;
import com.dosion.noisense.module.batchlog.repository.BatchResultLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardStatBuildTasklet implements Tasklet {

  private final BatchResultLogRepository logRepository;  // 작업 로그 저장용 Repository

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    log.info("📊 대시보드 통계 생성 작업 실행됨");

    // ✅ TODO: 여기에 통계 계산 + 통계 테이블 insert/update 로직 작성

    // 예: 하루 평균 소음, 최고 시간대, 민원 키워드 요약 등
    // ex: dashboard_district_noise_summary, dashboard_district_noise_hourly 등 갱신

    logRepository.save(
      BatchResultLog.builder()
        .jobName("hourlyNoiseJob")
        .message("하루 1회 대시보드 통계 작업")
        .build()
    );

    return RepeatStatus.FINISHED; // 작업은 1회 실행으로 종료
  }
}
