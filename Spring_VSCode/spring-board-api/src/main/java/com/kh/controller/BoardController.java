package com.kh.controller;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dto.BoardCommentDTO;
import com.kh.dto.BoardDTO;
import com.kh.dto.BoardFileDTO;
import com.kh.dto.BoardMemberDTO;
import com.kh.service.BoardService;
import com.kh.token.JwtTokenProvider;
import com.kh.vo.PaggingVO;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

	@GetMapping("/board/comment/{bno}")
	public List<BoardCommentDTO> getMethodName(@PathVariable int bno, @RequestParam int start) {
		List<BoardCommentDTO> commentList = boardService.getCommentList(bno, start);
		return commentList;
	}

	@GetMapping("/board/like/{bno}")
	public Map<String, Object> boardLike(@PathVariable int bno, @RequestHeader("Authorization") String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		token = token != null ? token.replace("Bearer ", "") : null;
		if (token == null) {
			map.put("code", 2);
			map.put("msg", "로그인 하셔야 이용하실수 있습니다.");
		} else {
			String id = tokenProvider.getUserIDFromToken(token);
			try {
				boardService.insertBoardLike(bno, id);
				map.put("code", 1);
				map.put("msg", "해당 게시글에 좋아요 하셨습니다.");
			} catch (Exception e) {
				boardService.deleteBoardLike(bno, id);
				map.put("code", 1);
				map.put("msg", "해당 게시글에 좋아요를 취소 하셨습니다.");
			}
			map.put("count", boardService.getBoardLike(bno));
		}
		return map;
	}

	@GetMapping("/board/hate/{bno}")
	public Map<String, Object> boardHate(@PathVariable int bno, @RequestHeader("Authorization") String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		token = token != null ? token.replace("Bearer ", "") : null;
		if (token == null) {
			map.put("code", 2);
			map.put("msg", "로그인 하셔야 이용하실수 있습니다.");
		} else {
			String id = tokenProvider.getUserIDFromToken(token);
			try {
				boardService.insertBoardHate(bno, id);
				map.put("code", 1);
				map.put("msg", "해당 게시글에 싫어요 하셨습니다.");
			} catch (Exception e) {
				boardService.deleteBoardHate(bno, id);
				map.put("code", 1);
				map.put("msg", "해당 게시글에 싫어요를 취소 하셨습니다.");
			}
			map.put("count", boardService.getBoardHate(bno));
		}
		return map;
	}

	@GetMapping("/board/comment/like/{cno}")
	public Map<String, Object> boardCommentLike(@PathVariable int cno, @RequestHeader("Authorization") String token) {
		Map<String, Object> map = new HashMap<String, Object>();

		token = token != null ? token.replace("Bearer ", "") : null;
		if (token == null) {
			map.put("code", 2);
			map.put("msg", "로그인 하셔야 이용하실수 있습니다.");
		} else {
			String id = tokenProvider.getUserIDFromToken(token);
			try {
				boardService.insertBoardCommentLike(cno, id);
				map.put("code", 1);
				map.put("msg", "해당 댓글에 좋아요 하셨습니다.");
			} catch (Exception e) {
				boardService.deleteBoardCommentLike(cno, id);
				map.put("code", 1);
				map.put("msg", "해당 댓글에 좋아요를 취소 하셨습니다.");
			}
			map.put("count", boardService.selectCommentLikeCount(cno));
		}
		return map;
	}

	@GetMapping("/board/comment/hate/{cno}")
	public Map<String, Object> boardCommentHate(@PathVariable int cno, @RequestHeader("Authorization") String token) {
		Map<String, Object> map = new HashMap<String, Object>();

		token = token != null ? token.replace("Bearer ", "") : null;
		if (token == null) {
			map.put("code", 2);
			map.put("msg", "로그인 하셔야 이용하실수 있습니다.");
		} else {
			String id = tokenProvider.getUserIDFromToken(token);
			try {
				boardService.insertBoardCommentHate(cno, id);
				map.put("code", 1);
				map.put("msg", "해당 댓글에 싫어요 하셨습니다.");
			} catch (Exception e) {
				boardService.deleteBoardCommentHate(cno, id);
				map.put("code", 1);
				map.put("msg", "해당 댓글에 싫어요를 취소 하셨습니다.");
			}
			map.put("count", boardService.selectCommentHateCount(cno));
		}
		return map;
	}

	@PostMapping("/board/comment")
	public Map<String, Object> boardCommentWrite(@RequestBody Map<String, Object> map,
			@RequestHeader("Authorization") String token) {
		Map<String, Object> result = new HashMap<String, Object>();

		token = token != null ? token.replace("Bearer ", "") : null;
		if (token == null) {
			result.put("code", 2);
			result.put("msg", "로그인 하셔야 이용하실수 있습니다.");
		} else {
			String id = tokenProvider.getUserIDFromToken(token);
			BoardCommentDTO comment = new BoardCommentDTO(Integer.parseInt(map.get("bno").toString()), id,
					map.get("content").toString());
			boardService.insertBoardComment(comment);
			result.put("code", 1);
			result.put("msg", "댓글 추가 완료");
			result.put("commentList", boardService.getCommentList(comment.getBno(), 1));
		}
		return result;
	}

	@DeleteMapping("/board/{bno}")
	public Map<String, Object> boardDelete(@PathVariable int bno, @RequestHeader("Authorization") String token) {
		Map<String, Object> map = new HashMap<>();
		token = token != null ? token.replace("Bearer ", "") : null;
		if (token != null && tokenProvider.getUserIDFromToken(token)
				.equals(boardService.selectBoard(bno).getId())) {
			// 첨부파일 삭제
			// 1. 파일 목록 받아옴
			List<BoardFileDTO> fileList = boardService.getBoardFileList(bno);
			// 2. 파일 삭제
			fileList.forEach(file -> {
				File f = new File(file.getFpath());
				f.delete();
			});
			// 만약 board, board_file 테이블이 외래키로 cascade 제약조건이 설정되어있지 않다면, 직접 board_file 테이블의
			// 데이터를 삭제해야함.
			boardService.deleteBoard(bno);
			map.put("code", 1);
			map.put("msg", "해당 게시글 삭제를 완료하였습니다.");
		} else {
			map.put("code", 2);
			map.put("msg", "게시글 삭제를 실패하였습니다.");
		}
		return map;
	}
	@PutMapping("/board/comment")
    public Map<String, Object> boardCommentUpdate(@RequestBody Map<String, String> body, @RequestHeader("Authorization") String token) {
      Map<String, Object> map = new HashMap<String, Object>();
      BoardCommentDTO comment = boardService.selectComment(Integer.parseInt(body.get("cno")));
  	  token = token != null ? token.replace("Bearer ", "") : null;

      if(token != null && tokenProvider.getUserIDFromToken(token)
				.equals(comment.getId())){
        comment.setContent(body.get("content"));
        boardService.updateBoardComment(comment);
        map.put("code", 1);
        map.put("msg", "해당 댓글 수정 완료");
        map.put("commentList", boardService.getCommentList(comment.getBno(), 1));
      }else{
        map.put("code", 2);
        map.put("msg", "해당 댓글 작성자만 수정이 가능합니다.");
      }
      return map;
    }
	
	@DeleteMapping("/board/comment/{cno}")
    public Map<String, Object> boardCommentDelete(@PathVariable int cno, @RequestHeader("Authorization") String token) {
      Map<String, Object> map = new HashMap<String, Object>();
      BoardCommentDTO comment = boardService.selectComment(cno);
  	  token = token != null ? token.replace("Bearer ", "") : null;

      if(token != null && tokenProvider.getUserIDFromToken(token)
				.equals(comment.getId())){
        boardService.deleteBoardComment(cno);
        map.put("code", 1);
        map.put("msg", "해당 댓글 삭제 완료");
        map.put("commentList", boardService.getCommentList(comment.getBno(), 1));
      }else{
        map.put("code", 2);
        map.put("msg", "해당 댓글 작성자만 삭제가 가능합니다.");
      }
      return map;
    }
}