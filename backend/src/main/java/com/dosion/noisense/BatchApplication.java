package com.dosion.noisense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
  scanBasePackages = {"com.dosion.noisense.batch"}
)
public class BatchApplication {
  public static void main(String[] args) {
    SpringApplication.run(BatchApplication.class, args);
  }
}
