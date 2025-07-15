package com.dosion.noisense.module.board.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.dosion.noisense.module.board.elasticsearch.repository.BoardEsRepository;
import com.dosion.noisense.web.board.elasticsearch.dto.BoardEsDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardEsService {

  private final ElasticsearchClient client;
  private final BoardEsRepository repository;

  public void save(BoardEsDocument document) {
    repository.save(document);
  }

  public void deleteById(String id) {
    repository.deleteById(id);
  }

  public Page<BoardEsDocument> search(String keyword, int page, int size) {
    try {
      int from = page * size;
      Query query;

      if (keyword == null || keyword.isBlank()) {
        query = MatchAllQuery.of(m -> m)._toQuery();
      } else {
        query = BoolQuery.of(b -> {
          b.should(PrefixQuery.of(p -> p.field("title").value(keyword))._toQuery());
          b.should(PrefixQuery.of(p -> p.field("content").value(keyword))._toQuery());
          b.should(PrefixQuery.of(p -> p.field("title.chosung").value(keyword))._toQuery());
          b.should(PrefixQuery.of(p -> p.field("content.chosung").value(keyword))._toQuery());
          b.should(MatchQuery.of(p -> p.field("title.ngram").query(keyword))._toQuery());
          b.should(MatchQuery.of(p -> p.field("content.ngram").query(keyword))._toQuery());

          // ✅ 닉네임(username) 검색 추가
          b.should(PrefixQuery.of(p -> p.field("username").value(keyword))._toQuery());
          b.should(MatchQuery.of(p -> p.field("username").query(keyword))._toQuery());

          if (keyword.length() >= 3) {
            b.should(MatchQuery.of(m -> m.field("title").query(keyword).fuzziness("AUTO"))._toQuery());
            b.should(MatchQuery.of(m -> m.field("content").query(keyword).fuzziness("AUTO"))._toQuery());
          }
          return b;
        })._toQuery();
      }

      SearchRequest request = SearchRequest.of(s -> s
        .index("board-index")
        .from(from)
        .size(size)
        .query(query)
        .sort(sort -> sort.field(f -> f.field("created_date").order(SortOrder.Desc)))
      );

      SearchResponse<BoardEsDocument> response = client.search(request, BoardEsDocument.class);

      List<BoardEsDocument> content = response.hits()
        .hits()
        .stream()
        .map(Hit::source)
        .collect(Collectors.toList());

      long total = response.hits().total().value();
      return new PageImpl<>(content, PageRequest.of(page, size), total);

    } catch (IOException e) {
      log.error("검색 오류", e);
      throw new RuntimeException("검색 중 오류 발생", e);
    }
  }
}
