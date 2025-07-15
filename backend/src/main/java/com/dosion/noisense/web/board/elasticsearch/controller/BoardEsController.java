package com.dosion.noisense.web.board.elasticsearch.controller;

import com.dosion.noisense.module.board.elasticsearch.service.BoardEsService;
import com.dosion.noisense.web.board.elasticsearch.dto.BoardEsDocument;
import com.dosion.noisense.web.board.elasticsearch.dto.BoardEsSearchRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/es/board")
@RequiredArgsConstructor
public class BoardEsController {

  private final BoardEsService boardEsService;

  /**
   * 게시판 Elasticsearch 검색
   */
  @GetMapping("/search")
  public Page<BoardEsDocument> searchBoards(@ModelAttribute BoardEsSearchRequestDto requestDto) {
    return boardEsService.search(
      requestDto.getKeyword(),
      requestDto.getPage(),
      requestDto.getSize()
    );
  }
}
