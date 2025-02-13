package com.kh.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

// BoardController 클래스: Spring Boot 기반의 REST API 컨트롤러로 게시판 기능을 담당합니다.
@RestController // @RestController는 Spring에서 RESTful 웹 서비스를 구현할 때 사용됩니다. @Controller와
				// @ResponseBody가 합쳐진 형태로, JSON 형식으로 응답을 반환합니다.
@CrossOrigin(origins = "*", allowedHeaders = "*") // @CrossOrigin은 다른 도메인에서의 요청을 허용합니다. '*'를 사용하면 모든 도메인의 요청을 허용합니다.
public class BoardController {

	// BoardService와 JwtTokenProvider 객체를 주입받습니다.
	private BoardService boardService; // 게시판 관련 비즈니스 로직을 처리하는 서비스 객체. 데이터베이스와 상호작용하여 게시판 데이터를 처리합니다.
	private JwtTokenProvider tokenProvider; // JWT 토큰 관련 기능을 처리하는 객체. 토큰 생성, 파싱, 인증 등을 수행합니다.

	// 생성자를 통해 필요한 의존성을 주입받습니다.
	public BoardController(BoardService boardService, JwtTokenProvider tokenProvider) {
		this.boardService = boardService; // BoardService를 초기화합니다.
		this.tokenProvider = tokenProvider; // JwtTokenProvider를 초기화합니다.
	}

	/**
	 * 게시글 목록 조회 메서드
	 * 
	 * @param pageNo        페이지 번호 (기본값: 1)
	 * @param pageContentEa 페이지당 게시글 수 (기본값: 30)
	 * @return 게시글 목록과 페이징 정보를 담은 Map
	 */
	@GetMapping("/board/list") // HTTP GET 요청에 매핑됩니다.
	public Map<String, Object> index(@RequestParam(defaultValue = "1") int pageNo, // 페이지 번호를 요청에서 받습니다.
			@RequestParam(defaultValue = "30") int pageContentEa) { // 페이지당 게시글 수를 요청에서 받습니다.
		// 전체 게시글 수 조회
		int count = boardService.selectBoardTotalCount(); // 데이터베이스에서 전체 게시글 수를 조회합니다.
		// 페이지 번호에 따라 게시글 목록 조회
		List<BoardDTO> list = boardService.getBoardList(pageNo, pageContentEa); // 데이터베이스에서 해당 페이지의 게시글 목록을 가져옵니다.
		// 페이징 정보 생성
		PaggingVO pagging = new PaggingVO(count, pageNo, pageContentEa); // 게시글 페이징 처리를 위한 객체를 생성합니다.

		// 데이터 반환을 위한 Map 생성
		Map<String, Object> map = new HashMap<>(); // 결과 데이터를 담을 Map 객체를 생성합니다.
		map.put("boardList", list); // 게시글 목록을 Map에 추가합니다.
		map.put("pagging", pagging); // 페이징 정보를 Map에 추가합니다.

		return map; // Map을 반환합니다.
	}

	/**
	 * 게시글 상세 조회 메서드
	 * 
	 * @param bno 게시글 번호
	 * @return 게시글, 댓글 목록, 파일 목록을 담은 Map
	 */
	@GetMapping("/board/{bno}") // URL 경로에서 {bno} 부분의 값을 변수로 매핑합니다.
	public Map<String, Object> boardDetail(@PathVariable int bno) { // 요청 경로에서 게시글 번호를 받아옵니다.
		Map<String, Object> map = new HashMap<>(); // 데이터를 담을 Map 생성
		BoardDTO board = boardService.selectBoard(bno); // 데이터베이스에서 게시글 정보를 조회합니다.
		List<BoardCommentDTO> commentList = boardService.getCommentList(bno, 1); // 데이터베이스에서 해당 게시글의 댓글 목록을 조회합니다.
		List<BoardFileDTO> fileList = boardService.getBoardFileList(bno); // 데이터베이스에서 해당 게시글의 파일 목록을 조회합니다.

		// Map에 데이터 추가
		map.put("board", board); // 조회한 게시글 정보를 Map에 추가합니다.
		map.put("commentList", commentList); // 조회한 댓글 목록을 Map에 추가합니다.
		map.put("fileList", fileList); // 조회한 파일 목록을 Map에 추가합니다.

		return map; // Map을 반환합니다.
	}

	/**
	 * 게시글 작성 메서드
	 * 
	 * @param token  JWT 인증 토큰 (헤더에서 전달받음)
	 * @param params 게시글 정보 (제목과 내용)
	 * @param files  첨부 파일 목록
	 * @return 작성 성공 여부와 메시지를 담은 Map
	 */
	@PostMapping("/board/write") // HTTP POST 요청에 매핑됩니다.
	public Map<String, Object> boardWrite(
			@RequestHeader("Authorization") String token, // 요청 헤더에서 JWT 토큰을 가져옵니다.
			@RequestPart("params") String params, // 요청의 일부로 전달된 게시글 정보를 JSON 문자열로 받습니다.
			@RequestPart(value = "files", required = false) MultipartFile[] files // 첨부 파일 배열
	) throws IllegalStateException, IOException {
		Map<String, Object> map = new HashMap<>(); // 반환할 데이터를 담을 Map 생성
		token = token != null ? token.replace("Bearer ", "") : null; // "Bearer " 접두어 제거

		ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper 객체 생성. Jackson 라이브러리를 사용합니다.
		// ObjectMapper는 JSON 데이터를 Java 객체로 변환하거나 그 반대로 변환할 때 사용됩니다.
		
		Map<String, String> paramsMap = objectMapper.readValue(params, new TypeReference<Map<String, String>>() {
		}); // JSON 문자열을 Map으로 변환
		// paramsMap: 게시글 작성 요청의 JSON 데이터를 파싱하여 제목(title)과 내용(content)을 Map 형태로 저장합니다.

		BoardDTO board = new BoardDTO(); // 게시글 정보를 담을 DTO 생성. DTO(Data Transfer Object)는 데이터 전달용 객체입니다.

		try {
			board.setId(tokenProvider.getUserIDFromToken(token)); // 토큰에서 사용자 ID를 추출하여 게시글 작성자 ID로 설정합니다.
			// tokenProvider: JWT 토큰을 파싱하거나 검증하여 사용자 ID 또는 기타 정보를 추출합니다.
		} catch (Exception e) { // 인증 실패 시 예외 처리
			map.put("msg", "로그인 하셔야 이용하실 수 있습니다."); // 오류 메시지를 Map에 추가
			map.put("code", 2); // 실패 코드 추가
			return map; // 실패 응답 반환
		}

		board.setTitle(paramsMap.get("title")); // 게시글 제목 설정. paramsMap에서 제목 데이터를 가져옵니다.
		board.setContent(paramsMap.get("content")); // 게시글 내용 설정. paramsMap에서 내용 데이터를 가져옵니다.

		int bno = boardService.selectBoardNo(); // 새로운 게시글 번호 생성. 데이터베이스에서 가장 최신 게시글 번호를 조회 후 증가된 번호를 반환합니다.
		board.setBno(bno); // 게시글 번호 설정

		// 첨부 파일 처리
		List<BoardFileDTO> fileList = new ArrayList<>(); // 첨부 파일 정보를 담을 리스트 생성
		File root = new File("C:\\fileupload"); // 파일 저장 경로 설정
		if (!root.exists()) { // 저장 경로가 존재하지 않으면
			root.mkdirs(); // 디렉토리 생성
		}
		if (files != null) { // 첨부 파일이 있을 경우
			for (MultipartFile file : files) { // 파일 배열을 반복 처리
				if (file.isEmpty()) { // 파일이 비어 있으면 건너뜁니다.
					continue;
				}
				String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename(); // 고유 파일 이름 생성
				String filePath = root + File.separator + fileName; // 저장할 파일 경로 생성
				file.transferTo(new File(filePath)); // 실제 파일 저장

				BoardFileDTO fileDTO = new BoardFileDTO(); // 파일 정보를 담을 DTO 생성
				fileDTO.setBno(bno); // 게시글 번호 설정
				fileDTO.setFpath(filePath); // 파일 경로 설정
				fileList.add(fileDTO); // 리스트에 파일 정보 추가
			}
		}

		int count = boardService.insertBoard(board, fileList); // 게시글과 첨부 파일 정보 저장
		if (count != 0) { // 저장 성공 여부 확인
			map.put("bno", bno); // 게시글 번호 추가
			map.put("code", 1); // 성공 코드 추가
			map.put("msg", "게시글 쓰기 성공"); // 성공 메시지 추가
		} else { // 저장 실패 처리
			map.put("msg", "게시글 쓰기 실패"); // 실패 메시지 추가
			map.put("code", 2); // 실패 코드 추가
		}

		return map; // 결과 반환
	}

	/**
	 * 파일 다운로드 메서드
	 * 
	 * @param fno      파일 번호
	 * @param response HTTP 응답 객체
	 * @return 다운로드할 파일 리소스를 포함한 ResponseEntity
	 */
	@GetMapping("board/download/{fno}")
	public ResponseEntity<Resource> fileDownload(@PathVariable int fno, HttpServletResponse response)
			throws FileNotFoundException, IOException {
		String filePath = boardService.selectFilePath(fno); // 파일 경로 조회
		File file = new File(filePath); // 파일 객체 생성
		String encodingFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8); // 파일명 인코딩
		Resource resource = new FileSystemResource(file); // 파일 리소스 생성
		String contentDisposition = "attachment;fileName=" + encodingFileName; // Content-Disposition 헤더 설정

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM) // 응답 본문 유형 설정
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition) // Content-Disposition 헤더 추가
				.body(resource); // 파일 리소스를 응답 본문에 추가
	}

	/**
	 * 코드 흐름 및 설명:
	 * 1. RESTful API 컨트롤러로, 클라이언트의 요청을 처리하고 데이터를 반환합니다.
	 * 2. @RestController와 @CrossOrigin을 통해 API 응답 형식과 CORS를 설정합니다.
	 * 3. 주요 메서드는 다음과 같습니다:
	 * - 게시글 목록 조회: 전체 게시글 목록과 페이징 정보를 반환합니다.
	 * - 게시글 상세 조회: 특정 게시글과 관련된 댓글 및 파일 목록을 반환합니다.
	 * - 게시글 작성: 새로운 게시글과 첨부 파일을 저장합니다.
	 * - 파일 다운로드: 파일 경로를 조회하고 해당 파일을 다운로드합니다.
	 * 4. 서비스 계층(BoardService)을 통해 데이터베이스 작업을 수행하며, 비즈니스 로직을 분리합니다.
	 * 5. JWT 토큰을 통해 인증 및 사용자 식별을 수행합니다.
	 */


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
	public Map<String, Object> boardCommentUpdate(@RequestBody Map<String, String> body,
			@RequestHeader("Authorization") String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		BoardCommentDTO comment = boardService.selectComment(Integer.parseInt(body.get("cno")));
		token = token != null ? token.replace("Bearer ", "") : null;

		if (token != null && tokenProvider.getUserIDFromToken(token)
				.equals(comment.getId())) {
			comment.setContent(body.get("content"));
			boardService.updateBoardComment(comment);
			map.put("code", 1);
			map.put("msg", "해당 댓글 수정 완료");
			map.put("commentList", boardService.getCommentList(comment.getBno(), 1));
		} else {
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

		if (token != null && tokenProvider.getUserIDFromToken(token)
				.equals(comment.getId())) {
			boardService.deleteBoardComment(cno);
			map.put("code", 1);
			map.put("msg", "해당 댓글 삭제 완료");
			map.put("commentList", boardService.getCommentList(comment.getBno(), 1));
		} else {
			map.put("code", 2);
			map.put("msg", "해당 댓글 작성자만 삭제가 가능합니다.");
		}
		return map;
	}

}