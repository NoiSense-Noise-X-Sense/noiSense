package com.dosion.noisense.common.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardIndexInitializer implements ApplicationRunner {

  private final ElasticsearchClient client;

  @Override
  public void run(ApplicationArguments args) {
    try {
      log.info("🔍 board-index 존재 확인 중...");
      boolean exists = client.indices().exists(e -> e.index("board-index")).value();
      log.info("🔍 board-index 존재 여부: {}", exists);

      if (!exists) {
        File file = new File("../Elasticsearch/board-index.json");
        if (!file.exists()) {
          log.error("❌ board-index.json 파일을 찾을 수 없습니다: {}", file.getAbsolutePath());
          throw new RuntimeException("board-index.json 파일을 찾을 수 없음: " + file.getAbsolutePath());
        }

        String json = new BufferedReader(new FileReader(file))
          .lines().collect(Collectors.joining("\n"));

        CreateIndexRequest request = CreateIndexRequest.of(c -> c
          .index("board-index")
          .withJson(new StringReader(json))
        );

        CreateIndexResponse response = client.indices().create(request);
        if (response.acknowledged()) {
          log.info("✅ Elasticsearch 'board-index' 생성 완료.");
        } else {
          log.warn("⚠️ Elasticsearch 'board-index' 생성 실패 (ack=false).");
          throw new RuntimeException("Elasticsearch 'board-index' 생성 실패 (ack=false)");
        }
      } else {
        log.info("📌 Elasticsearch 'board-index' 이미 존재함.");
      }

    } catch (Exception e) {
      log.error("❌ Elasticsearch 인덱스 생성 실패", e);
      throw new RuntimeException("Elasticsearch 인덱스 생성 중 오류", e);
    }
  }
}
