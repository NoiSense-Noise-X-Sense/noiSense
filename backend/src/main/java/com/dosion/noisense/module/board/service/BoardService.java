package com.dosion.noisense.module.board.service;

import com.dosion.noisense.common.ai.service.EmotionScoreService;
import com.dosion.noisense.module.api.entity.AutonomousDistrictEntity;
import com.dosion.noisense.module.api.repository.AutonomousDistrictRepository;
import com.dosion.noisense.module.board.elasticsearch.service.BoardEsService;
import com.dosion.noisense.web.board.elasticsearch.dto.BoardEsDocument;
import com.dosion.noisense.module.board.entity.BoardEmpathy;
import com.dosion.noisense.module.board.repository.BoardEmpathyRepository;
import com.dosion.noisense.web.board.dto.BoardDto;
import com.dosion.noisense.module.board.entity.Board;
import com.dosion.noisense.module.board.repository.BoardRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final BoardEmpathyRepository boardEmpathyRepository;
  private final BoardEsService boardEsService;
  private final EmotionScoreService emotionScoreService;
  private final AutonomousDistrictRepository autonomousDistrictRepository;
  private Map<String, String> codeToNameMap;


  /** 게시글 작성 **/
  @Transactional
  public BoardDto createBoard(BoardDto boardDto) {
    //AI 감정점수 분석
    int score = emotionScoreService.evaluateEmotionScore(boardDto.getContent());

    Board board = Board.builder()
      .userId(boardDto.getUserId())
      .nickname(boardDto.getNickname())
      .title(boardDto.getTitle())
      .content(boardDto.getContent())
      .emotionalScore((long) score)
      .empathyCount(boardDto.getEmpathyCount())
      .viewCount(boardDto.getViewCount() != null ? boardDto.getViewCount() : 0L)
      .autonomousDistrictCode(boardDto.getAutonomousDistrict()) // 코드(UUID)만 저장
      .administrativeDistrictCode(boardDto.getAdministrativeDistrict())
      .createdDate(LocalDateTime.now())
      .modifiedDate(LocalDateTime.now())
      .build();

    Board savedBoard = boardRepository.save(board);
    BoardDto resultDto = toDTO(savedBoard);

    //Elasticsearch 저장
    boardEsService.save(BoardEsDocument.builder()
      .id(String.valueOf(savedBoard.getId()))
      .title(savedBoard.getTitle())
      .content(savedBoard.getContent())
      .username(savedBoard.getNickname())
      .userId(savedBoard.getUserId())
      .autonomousDistrict(savedBoard.getAutonomousDistrictCode())
      .createdDate(savedBoard.getCreatedDate().atZone(ZoneId.of("Asia/Seoul")).toInstant())
      .modifiedDate(savedBoard.getModifiedDate().atZone(ZoneId.of("Asia/Seoul")).toInstant())
      .view_count(savedBoard.getViewCount())
      .build());

    return toDTO(savedBoard);
  }

  /** 게시글 상세 조회 및 조회수 증가 **/
  @Transactional
  public BoardDto getBoardById(Long id) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. ID: " + id));

    // DB 조회수 증가
    board.setViewCount(board.getViewCount() + 1);
    board.setModifiedDate(LocalDateTime.now());

    Board updatedBoard = boardRepository.save(board);
    BoardDto resultDto = toDTO(updatedBoard);

    // ElasticSearch 조회수 증가
    boardEsService.save(BoardEsDocument.builder()
      .id(String.valueOf(updatedBoard.getId()))
      .title(updatedBoard.getTitle())
      .content(updatedBoard.getContent())
      .username(updatedBoard.getNickname())
      .userId(updatedBoard.getUserId())
      .createdDate(updatedBoard.getCreatedDate().atZone(ZoneId.of("Asia/Seoul")).toInstant())
      .modifiedDate(updatedBoard.getModifiedDate().atZone(ZoneId.of("Asia/Seoul")).toInstant())
      .view_count(updatedBoard.getViewCount())
      .build());

    return resultDto;
  }

  /** 게시글 수정 **/
  @Transactional
  public BoardDto updateBoard(Long id, Long userId, BoardDto boardDto) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

    if (!board.getUserId().equals(userId)) {
      throw new IllegalArgumentException("수정 권한이 없습니다.");
    }

    board.setTitle(boardDto.getTitle());
    board.setContent(boardDto.getContent());

    //감정점수 다시 평가 (수정된 content 기준)
    int score = emotionScoreService.evaluateEmotionScore(boardDto.getContent());
    board.setEmotionalScore((long) score);

    board.setEmpathyCount(boardDto.getEmpathyCount());
    board.setViewCount(boardDto.getViewCount());
    board.setAutonomousDistrictCode(boardDto.getAutonomousDistrict());
    board.setAdministrativeDistrictCode(boardDto.getAdministrativeDistrict());
    board.setModifiedDate(LocalDateTime.now());

    Board updatedBoard = boardRepository.save(board);
    return toDTO(updatedBoard);
  }

  /** 게시글 삭제 **/
  @Transactional
  public void deleteBoard(Long id, Long userId) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

    if (!board.getUserId().equals(userId)) {
      throw new IllegalArgumentException("삭제 권한이 없습니다.");
    }

    boardRepository.delete(board);
  }

  /** 게시글 페이징 목록 조회 **/
  @Transactional(readOnly = true)
  public Page<BoardDto> getBoards(int page, int size) {
    return boardRepository.findAllPaging(PageRequest.of(page, size));
  }

  @Transactional
  public void toggleEmpathyCount(Long boardId, Long userId) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. ID: " + boardId));

    Optional<BoardEmpathy> empathyOptional =
      boardEmpathyRepository.findByBoardIdAndUserId(boardId, userId);

    Long enpathyCnt = board.getEmpathyCount() != null ? board.getEmpathyCount() : 0;
    if (empathyOptional.isPresent()) {
      // 이미 공감한 상태 → 삭제 및 empathy_count -1
      boardEmpathyRepository.delete(empathyOptional.get());

      board.setEmpathyCount(enpathyCnt > 0 ? enpathyCnt - 1 : 0);
    } else {
      // 공감하지 않은 상태 → 추가 및 empathy_count +1
      BoardEmpathy boardEmpathy = BoardEmpathy.builder()
        .boardId(boardId)
        .userId(userId)
        .createdDate(LocalDateTime.now())
        .build();
      boardEmpathyRepository.save(boardEmpathy);
      board.setEmpathyCount(enpathyCnt + 1);
    }
    boardRepository.save(board); // 공감 수 저장
  }

  @PostConstruct
  public void initCodeToNameMap() {
    this.codeToNameMap = autonomousDistrictRepository.findAll().stream()
      .collect(Collectors.toMap(
        AutonomousDistrictEntity::getCode,
        AutonomousDistrictEntity::getNameKo
      ));
  }

  /** Entity → DTO 변환 **/
  private BoardDto toDTO(Board board) {
    String code = board.getAutonomousDistrictCode();
    String nameKo = codeToNameMap.getOrDefault(code, "알 수 없음");

    return BoardDto.builder()
      .boardId(board.getId())
      .userId(board.getUserId())
      .nickname(board.getNickname())
      .title(board.getTitle())
      .content(board.getContent())
      .emotionalScore(board.getEmotionalScore())
      .empathyCount(board.getEmpathyCount())
      .viewCount(board.getViewCount())
      .autonomousDistrict(code)         // 자치구 코드
      .autonomousDistrictName(nameKo)   // 자치구 한글 이름 (응답용)
      .administrativeDistrict(board.getAdministrativeDistrictCode())
      .createdDate(board.getCreatedDate())
      .modifiedDate(board.getModifiedDate())
      .build();
  }

  /** 특정 사용자의 게시글 페이징 목록 조회 **/
  @Transactional(readOnly = true)
  public Page<BoardDto> getBoardsByUserId(Long userId, int page, int size) {
    return boardRepository.findByUserIdPaging(userId, PageRequest.of(page, size));
  }

}
