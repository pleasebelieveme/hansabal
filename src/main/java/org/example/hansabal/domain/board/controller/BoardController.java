package org.example.hansabal.domain.board.controller;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.board.dto.BoardRequestDto;
import org.example.hansabal.domain.board.dto.BoardResponseDto;
import org.example.hansabal.domain.board.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;



}
