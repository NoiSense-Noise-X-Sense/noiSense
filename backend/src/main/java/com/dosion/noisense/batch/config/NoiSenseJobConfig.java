package com.dosion.noisense.batch.config;

import com.dosion.noisense.batch.tasklet.ApiDataFetchTasklet;
import com.dosion.noisense.batch.tasklet.DashboardStatBuildTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@RequiredArgsConstructor
public class NoiSenseJobConfig {

  /**
   * ✅ hourlyNoiseJob
   * - Step 1: 센서 API 수집
   * - Step 2: 오늘 첫 실행이면 통계 Step도 실행
   */
  @Bean
  public Job hourlyNoiseJob(JobRepository jobRepository,
                            Step apiStep,
                            Step statStep,
                            JobExecutionDecider dashboardTriggerDecider) {

    Flow decisionFlow = new FlowBuilder<Flow>("dashboardDecisionFlow")
      .start(dashboardTriggerDecider)
      .on("EXECUTE_DASHBOARD").to(statStep)
      .from(dashboardTriggerDecider)
      .on("SKIP_DASHBOARD").end()
      .build();

    return new JobBuilder("hourlyNoiseJob", jobRepository)
      .incrementer(new RunIdIncrementer())
      .start(apiStep)
      .on("*").to(decisionFlow)  // 💡 이렇게 연결
      .end()
      .build();
  }


  /** Step 1: 센서 API 수집 Step */
  @Bean
  public Step apiStep(JobRepository jobRepository,
                      PlatformTransactionManager txManager,
                      ApiDataFetchTasklet tasklet) {
    return new StepBuilder("apiStep", jobRepository)
      .tasklet(tasklet, txManager)
      .build();
  }

  /** Step 2: 대시보드 통계 생성 Step */
  @Bean
  public Step statStep(JobRepository jobRepository,
                       PlatformTransactionManager txManager,
                       DashboardStatBuildTasklet tasklet) {
    return new StepBuilder("statStep", jobRepository)
      .tasklet(tasklet, txManager)
      .build();
  }
}
