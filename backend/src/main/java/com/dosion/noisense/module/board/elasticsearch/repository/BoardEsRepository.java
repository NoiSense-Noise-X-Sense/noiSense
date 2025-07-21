package com.dosion.noisense.module.board.elasticsearch.repository;

import com.dosion.noisense.web.board.elasticsearch.dto.BoardEsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardEsRepository extends ElasticsearchRepository<BoardEsDocument, String> {
  List<BoardEsDocument> findByTitleContainingOrContentContaining(String title, String content);
}
