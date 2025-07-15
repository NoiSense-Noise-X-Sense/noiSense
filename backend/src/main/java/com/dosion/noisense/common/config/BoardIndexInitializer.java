package com.dosion.noisense.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 애플리케이션 실행 시 board-index가 없다면 자동으로 생성하는 컴포넌트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BoardIndexInitializer implements ApplicationRunner {

  private final ElasticsearchClient client;

  @Override
  public void run(ApplicationArguments args) {
    try {
      boolean exists = client.indices().exists(e -> e.index("board-index")).value();

      if (!exists) {
        // 💡 절대 경로 또는 상대 경로로 파일 직접 읽기 (루트 기준으로 이동)
        File file = new File("../Elasticsearch/board-index.txt");
        if (!file.exists()) {
          log.error("❌ board-index.txt 파일을 찾을 수 없습니다: {}", file.getAbsolutePath());
          return;
        }

        String json = new BufferedReader(new FileReader(file))
          .lines().collect(Collectors.joining("\n"));

        CreateIndexRequest request = CreateIndexRequest.of(c -> c
          .index("board-index")
          .withJson(new java.io.StringReader(json))
        );

        CreateIndexResponse response = client.indices().create(request);
        if (response.acknowledged()) {
          log.info("✅ Elasticsearch 'board-index' 생성 완료.");
        } else {
          log.warn("⚠️ Elasticsearch 'board-index' 생성 실패 (ack=false).");
        }
      } else {
        log.info("📌 Elasticsearch 'board-index' 이미 존재함.");
      }
    } catch (IOException e) {
      log.error("❌ Elasticsearch 인덱스 생성 실패", e);
    }
  }
}
