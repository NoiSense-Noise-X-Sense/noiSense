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
public class ApiDataFetchTasklet implements Tasklet {

  private final BatchResultLogRepository logRepository;  // 작업 로그 저장용 Repository

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    log.info("✅ API 3개 데이터 수집 작업 실행됨");

    // ✅ TODO: 여기에 실제 API 호출 → 파싱 → DB 저장 로직 구현 예정

    // 예시: 수집 완료 후 로그 테이블에 성공 기록
    logRepository.save(
      BatchResultLog.builder()
        .jobName("hourlyNoiseJob")
        .message("센서 API 데이터 수집 완료")
        .build()
    );

    return RepeatStatus.FINISHED; // 한 번 실행하고 끝냄
  }
}
