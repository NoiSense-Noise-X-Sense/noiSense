package com.dosion.noisense.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.dosion.noisense.batch.scheduler.HourlyNoiseJobScheduler;

@Component
@RequiredArgsConstructor
public class BatchJobLauncher implements CommandLineRunner {

  private final HourlyNoiseJobScheduler hourlyNoiseJobScheduler;

  @Override
  public void run(String... args) {
    try {
      hourlyNoiseJobScheduler.run();
    } finally {
      // 배치 완료 후 명시적으로 앱 종료!
      System.exit(0);
    }
  }
}
