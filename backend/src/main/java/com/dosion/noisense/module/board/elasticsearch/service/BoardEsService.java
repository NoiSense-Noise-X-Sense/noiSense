package com.dosion.noisense.module.board.elasticsearch.service;

import com.dosion.noisense.module.board.elasticsearch.repository.BoardEsRepository;
import com.dosion.noisense.web.board.dto.BoardDto;
import com.dosion.noisense.web.board.elasticsearch.dto.BoardEsDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class BoardEsService {

  private final BoardEsRepository repository;

  public void save(BoardEsDocument document) {
    repository.save(document);
  }

  public void saveBoardToElasticsearch(BoardDto dto) {
    BoardEsDocument document = BoardEsDocument.from(dto);
    repository.save(document);
  }


  /** 통합 검색 (title or content에 keyword 포함) **/
  public Page<BoardEsDocument> search(String keyword, int page, int size) {
    List<BoardEsDocument> results =
      repository.findByTitleContainingOrContentContaining(keyword, keyword);

    int start = Math.min(page * size, results.size());
    int end = Math.min(start + size, results.size());

    List<BoardEsDocument> pageContent = results.subList(start, end);
    return new PageImpl<>(pageContent, PageRequest.of(page, size), results.size());
  }

  /** 자주 등장하는 단어 직접 집계 **/
  public Map<String, Long> getFrequentWords(String autonomousDistrict,
                                            LocalDateTime startDate,
                                            LocalDateTime endDate,
                                            int size) {

    List<BoardEsDocument> all = StreamSupport
      .stream(repository.findAll().spliterator(), false)
      .collect(Collectors.toList());

    // 1. 필터링
    List<BoardEsDocument> filtered = all.stream()
      .filter(doc -> {
        if (autonomousDistrict != null && !autonomousDistrict.isBlank()) {
          if (!autonomousDistrict.equals(doc.getAutonomousDistrict())) return false;
        }
        if (startDate != null && endDate != null && doc.getCreatedDate() != null) {
          // Instant → LocalDateTime (KST 기준)
          LocalDateTime created = LocalDateTime.ofInstant(doc.getCreatedDate(), ZoneId.of("Asia/Seoul"));
          if (created.isBefore(startDate) || created.isAfter(endDate)) return false;
        }
        return true;
      })
      .collect(Collectors.toList());

    // 2. content에서 단어 추출 및 카운팅
    Map<String, Long> wordCount = new HashMap<>();
    for (BoardEsDocument doc : filtered) {
      String content = doc.getContent();
      if (content == null) continue;

      String[] words = content.split("\\s+");
      for (String word : words) {
        String clean = word.replaceAll("[^가-힣a-zA-Z0-9]", "");
        if (clean.length() <= 1) continue; // 한 글자 제외

        wordCount.put(clean, wordCount.getOrDefault(clean, 0L) + 1);
      }
    }

    // 3. 상위 size개 정렬 후 반환
    return wordCount.entrySet().stream()
      .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
      .limit(size)
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue,
        (e1, e2) -> e1,
        LinkedHashMap::new
      ));
  }
}
