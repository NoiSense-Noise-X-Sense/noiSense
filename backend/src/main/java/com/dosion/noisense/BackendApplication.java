package com.dosion.noisense;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
  scanBasePackages = {
    "com.dosion.noisense.common",
    "com.dosion.noisense.module",
    "com.dosion.noisense.web",
    "com.dosion.noisense.config"
  }
)
@Slf4j
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
    log.info("Noisense backend started");
	}

}
