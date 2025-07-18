package com.dosion.noisense.batch.util;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersIncrementer;

// Spring Batch run.id UUID 호환을 위한 StringRunIdIncrementer 적용
public class StringRunIdIncrementer implements JobParametersIncrementer {
  @Override
  public JobParameters getNext(JobParameters parameters) {
    return parameters;
  }
}
