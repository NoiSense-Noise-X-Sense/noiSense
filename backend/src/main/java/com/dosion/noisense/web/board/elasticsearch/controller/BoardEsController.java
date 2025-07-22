package com.dosion.noisense.web.board.elasticsearch.controller;

import com.dosion.noisense.module.board.elasticsearch.service.BoardEsService;
import com.dosion.noisense.web.board.dto.BoardDto;
import com.dosion.noisense.web.board.elasticsearch.dto.BoardEsDocument;
import com.dosion.noisense.web.board.elasticsearch.dto.BoardEsSearchRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/es/board")
@RequiredArgsConstructor
public class BoardEsController {

  private final BoardEsService boardEsService;

  @PostMapping
  public ResponseEntity<Void> saveBoardToElasticsearch(@RequestBody BoardDto boardDto) {
    boardEsService.saveBoardToElasticsearch(boardDto);
    return ResponseEntity.ok().build();
  }

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

  /**
   * getFrequentWords(...) 테스트용 코드 대시보드와 연결 후 삭제
   **/
  @GetMapping("/frequent-words")
  public ResponseEntity<Map<String, Long>> getFrequentWords(
    @RequestParam(required = false) String autonomousDistrict,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
    @RequestParam(defaultValue = "10") int size) {

    Map<String, Long> result = boardEsService.getFrequentWords(autonomousDistrict, startDate, endDate, size);
    return ResponseEntity.ok(result);
  }


}
