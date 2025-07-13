package com.dosion.noisense.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class HourlyNoiseJobScheduler {

  private final Job hourlyNoiseJob; // 실행할 Job 정의 (NoiSenseJobConfig 에서 등록됨)
  private final JobLauncher jobLauncher;

  // 매시간 정각마다 실행됨 (ex: 00:00, 01:00, 02:00..)
  @Scheduled(cron = "0 0 * * * *")
  //@Scheduled(cron = "0 * * * * *") // 매 1분마다 실행 (테스트용)

  public void run() {
    try{
      JobParameters params = new JobParametersBuilder()
        // JobParameters가 동일하면 JobInstance 중복 오류 발생하므로 고유한 값으로 생성
        .addString("run.id", UUID.randomUUID().toString())
        .toJobParameters();

      log.info("🚀 hourlyNoiseJob 배치 실행 시작");
      jobLauncher.run(hourlyNoiseJob, params);  // ✅ 실제 실행 호출

    }catch (Exception e){
      log.error("❌ hourlyNoiseJob 실행 실패", e);
    }
  }
}
