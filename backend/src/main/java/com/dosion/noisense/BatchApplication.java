package com.dosion.noisense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
  scanBasePackages = {
    "com.dosion.noisense.batch",
    "com.dosion.noisense.web.api.controller",
    "com.dosion.noisense.module.api",
    "com.dosion.noisense.module.batchlog",
    "com.dosion.noisense.module.dashboard",
    "com.dosion.noisense.config",
    "com.dosion.noisense.module.district"
  }
)
public class BatchApplication {
  public static void main(String[] args) {
    SpringApplication.run(BatchApplication.class, args);
  }
}
