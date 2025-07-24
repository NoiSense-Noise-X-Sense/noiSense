package com.dosion.noisense.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class HourlyNoiseJobScheduler {

  private final Job hourlyNoiseJob; // 실행할 Job 정의 (NoiSenseJobConfig 에서 등록됨)
  private final JobLauncher jobLauncher;


  public void run() {
    try{
      String uuid = UUID.randomUUID().toString();

      JobParameters params = new JobParametersBuilder()
        // JobParameters가 동일하면 JobInstance 중복 오류 발생하므로 고유한 값으로 생성
        .addString("run.id", uuid)
        .toJobParameters();

      log.info("[HourlyNoiseJobScheduler][True] hourlyNoiseJob 배치 실행 시작");
      jobLauncher.run(hourlyNoiseJob, params);  // ✅ 실제 실행 호출

    }catch (Exception e){
      log.error("[HourlyNoiseJobScheduler][Fail] hourlyNoiseJob 실행 실패", e);
    }
  }
}
