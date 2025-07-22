package com.dosion.noisense.batch.tasklet;

import com.dosion.noisense.module.batchlog.entity.BatchResultLog;
import com.dosion.noisense.module.batchlog.repository.BatchResultLogRepository;
import com.dosion.noisense.module.dashboard.service.DashboardStatService;
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

  private final DashboardStatService dashboardStatService;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {


    dashboardStatService.updateNoiseSummary();
    dashboardStatService.updateNoiseHourly();
    dashboardStatService.updateNoiseYearly();
    //지도 관련 작업 dashboardStatService.updateZoneNoise();


    logRepository.save(
      BatchResultLog.builder()
        .jobName("hourlyNoiseJob")
        .message("하루 1회 대시보드 통계 작업")
        .build()
    );

    log.info("[DashboardStatBuildTasklet][Sucess] 대시보드 통계 생성 작업 실행완료");

    return RepeatStatus.FINISHED; // 작업은 1회 실행으로 종료
  }
}
