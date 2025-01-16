package com.kh.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kh.dto.BoardCommentDTO;
import com.kh.dto.BoardDTO;
import com.kh.dto.BoardFileDTO;
import com.kh.service.BoardService;
import com.kh.token.JwtTokenProvider;
import com.kh.vo.PaggingVO;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BoardController {

	private BoardService boardService;
	private JwtTokenProvider tokenProvider;

	public BoardController(BoardService boardService, JwtTokenProvider tokenProvider) {
		this.boardService = boardService;
		this.tokenProvider = tokenProvider;
	}

	@GetMapping("/board/list")
	public Map<String, Object> index(@RequestParam(defaultValue = "1") int pageNo,
			@RequestParam(defaultValue = "30") int pageContentEa) {
		System.out.println(pageNo);
		// 전체 게시글 개수 조회
		int count = boardService.selectBoardTotalCount();
		// 페이지 번호를 보내서 해당 페이지 게시글 목록만 조회
		List<BoardDTO> list = boardService.getBoardList(pageNo, pageContentEa);
		// PaggingVO 페이징 정보 생성
		PaggingVO pagging = new PaggingVO(count, pageNo, pageContentEa);
		// 데이터 추가
		Map<String, Object> map = new HashMap<>();
		map.put("boardList", list);
		map.put("pagging", pagging);

		return map;
	}

	@GetMapping("/board/{bno}")
	public Map<String, Object> boardDetail(@PathVariable int bno) {
		Map<String, Object> map = new HashMap<>();
		BoardDTO board = boardService.selectBoard(bno);
		List<BoardCommentDTO> commentList = boardService.getCommentList(bno, 1);
		List<BoardFileDTO> fileList = boardService.getBoardFileList(bno);
		
		map.put("board", board);
		map.put("commentList", commentList);
		map.put("fileList", fileList);

		return map;
	}

}