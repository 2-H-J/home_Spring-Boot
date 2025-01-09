package com.kh.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.dto.BoardCommentDTO;
import com.kh.dto.BoardDTO;
import com.kh.service.BoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;


@RequestMapping("/board")
@Controller
public class BoardController {
  
  private BoardService boardService;

  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }

  @GetMapping("/{bno}")
  public ModelAndView boardDetail(@PathVariable int bno, ModelAndView view) {
    BoardDTO board = boardService.selectBoard(bno);
    List<BoardCommentDTO> commentList = boardService.getCommentList(bno);

    view.addObject("board", board);
    view.addObject("commentList", commentList);
    view.setViewName("board_view");

    return view;
  }
  
  
}