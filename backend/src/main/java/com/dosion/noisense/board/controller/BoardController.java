package com.dosion.noisense.board.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board-Controller", description = "게시판")
@RestController
@RequestMapping("/api/boards")
public class BoardController {

  @GetMapping
  public String board() {
    return "hello world";
  }
}
