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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dto.BoardCommentDTO;
import com.kh.dto.BoardDTO;
import com.kh.dto.BoardFileDTO;
import com.kh.dto.BoardMemberDTO;
import com.kh.service.BoardService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// BoardController 클래스: "/board" 경로에 대한 요청을 처리하는 컨트롤러 클래스 
@RequestMapping("/board") // 이 클래스의 모든 메서드는 기본적으로 "/board" 경로와 매핑됩니다.
@Controller // 이 클래스가 Spring MVC의 컨트롤러임을 나타냅니다.
public class BoardController {

  // 서비스 객체 주입: 비즈니스 로직 처리를 담당하는 서비스 클래스(BoardService)를 의존성 주입받음
  private BoardService boardService;

  // 생성자 주입 방식: BoardController의 생성자를 통해 BoardService 객체를 주입받습니다.
  // @Autowired를 사용하지 않고 생성자를 통해 의존성을 주입하는 방식입니다.
  public BoardController(BoardService boardService) {
    this.boardService = boardService; // 생성자를 통해 주입받은 객체를 초기화합니다.
  }

  // 특정 게시글의 상세 정보를 조회하고 조회수를 증가시키는 메서드 -------------------------------------------------
  @GetMapping("/{bno}") // HTTP GET 요청이 "/board/{bno}"로 들어올 경우 이 메서드가 호출됩니다.
  public ModelAndView boardDetail(@PathVariable int bno, ModelAndView view, HttpSession session) {
    // @PathVariable: URL 경로에 포함된 {bno} 값을 메서드의 매개변수 bno로 전달합니다.
    // ModelAndView: 데이터를 전달(Model)하고 뷰(View)를 설정하는 객체입니다.
    // HttpSession: 사용자의 세션 데이터를 관리하는 객체입니다.

    // 1. 조회수 증가 처리
    @SuppressWarnings("unchecked") // "unchecked" 경고를 무시합니다.
    HashSet<Integer> visited = (HashSet<Integer>) session.getAttribute("visited");
    // 세션에서 "visited"라는 이름의 속성을 가져옵니다.
    // visited는 사용자가 조회한 게시글 번호를 저장하는 HashSet<Integer>입니다.
    // 세션(session): 사용자별로 상태를 유지하는 저장소입니다. 예를 들어, 로그인 정보를 저장합니다.

    if (visited == null) { // 사용자가 처음 방문한 경우
      visited = new HashSet<Integer>(); // 새로운 HashSet 객체를 생성
      session.setAttribute("visited", visited); // 세션에 "visited" 속성으로 저장
    }

    if (visited.add(bno)) {
      // visited.add(bno): 현재 게시글 번호(bno)가 HashSet에 추가됩니다.
      // 이미 방문한 게시글은 HashSet에 저장되지 않으므로 중복 조회 방지가 가능합니다.
      // 만약 HashSet에 추가되었다면(중복되지 않으면), 게시글 조회수를 증가시킵니다.

      boardService.updateBoardCount(bno); // Service 계층을 호출하여 게시글의 조회수를 증가시킵니다.
    }

    // 2. 게시글 상세 정보 조회
    BoardDTO board = boardService.selectBoard(bno);
    // BoardService의 selectBoard(bno)를 호출하여 게시글 정보를 조회합니다.
    // board 객체는 게시글 번호(bno)에 해당하는 제목, 내용, 작성자, 조회수 등을 포함합니다.

    // 3. 댓글 리스트 조회
    List<BoardCommentDTO> commentList = boardService.getCommentList(bno, 1);
    // 게시글 번호(bno)에 해당하는 댓글 리스트를 가져옵니다.
    // 첫 번째 페이지(댓글 시작점)부터 5개 댓글을 조회하는 로직으로 구현되어 있을 가능성이 높습니다.
    // 반환값은 List<BoardCommentDTO> 형태입니다.

    // 4. 게시글에 첨부된 파일 리스트 조회
    List<BoardFileDTO> fileList = boardService.getBoardFileList(bno);
    // 게시글 번호(bno)에 첨부된 파일 정보를 가져옵니다.
    // 반환값은 List<BoardFileDTO> 형태로, 파일의 경로, 파일명 등의 정보를 포함합니다.

    // 5. ModelAndView 객체에 데이터 추가
    view.addObject("board", board); // 게시글 정보를 "board"라는 키로 저장
    view.addObject("commentList", commentList); // 댓글 리스트를 "commentList"라는 키로 저장
    view.addObject("fileList", fileList); // 첨부 파일 리스트를 "fileList"라는 키로 저장
    view.setViewName("board_view"); // 뷰 이름을 "board_view"로 설정
    // setViewName("board_view"): Spring에서 "board_view.html"과 같은 템플릿 파일을 렌더링하도록
    // 설정합니다.
    // addObject(): 뷰에 전달할 데이터를 추가합니다.

    // 6. 결과 반환
    return view;
    // ModelAndView 객체를 반환합니다.
    // - 데이터("board", "commentList", "fileList")는 뷰("board_view")에 전달되어 출력됩니다.
    // - 클라이언트는 "/board/{bno}" 요청에 대해 게시글 상세 페이지를 응답받습니다.

  }

  // 게시물 좋아요 증가/감소 로직을 처리하는 메서드 -----------------------------------------------------------------------------------
  @ResponseBody
  @GetMapping("/like/{bno}") // HTTP GET 요청이 "/like/{bno}"로 들어오면 이 메서드가 실행됩니다.
  public Map<String, Object> boardLike(@PathVariable int bno, HttpSession session) {
    // @ResponseBody: 반환값을 JSON 형식으로 클라이언트에 반환합니다.
    // @GetMapping: HTTP GET 요청을 처리하며, URL 경로에서 {bno} 값을 매핑하여 메서드 매개변수로 전달합니다.
    // bno: 좋아요를 처리할 게시글 번호입니다.
    // session: 사용자 세션 정보를 담고 있습니다.

    // 결과 데이터를 저장할 맵 객체 생성
    Map<String, Object> map = new HashMap<String, Object>();
    // map: 결과 데이터를 저장할 Map 객체입니다.
    // 반환될 데이터는 JSON 형식으로 변환됩니다.
    // 예: {"code": 1, "msg": "성공 메시지", "count": 좋아요 수}

    // 1. 사용자 로그인 여부 확인
    if (session.getAttribute("user") == null) {
      // session.getAttribute("user"): 세션에서 "user"라는 속성을 가져옵니다.
      // null이면 사용자가 로그인하지 않은 상태로 판단됩니다.
      map.put("code", "2"); // "2"는 로그인 필요 상태를 나타냅니다.
      map.put("msg", "로그인 하셔야 이용하실수 있습니다."); // 사용자에게 표시할 메시지
    } else {
      // 2. 로그인한 사용자 ID 가져오기
      String id = (String) ((BoardMemberDTO) session.getAttribute("user")).getId();
      // session.getAttribute("user"): 세션에서 로그인한 사용자의 정보를 가져옵니다.
      // BoardMemberDTO: 사용자 정보를 저장하는 DTO 객체로, 여기서 사용자의 ID를 추출합니다.

      try {
        // 3. 좋아요 추가 로직
        boardService.insertBoardLike(bno, id);
        // boardService.insertBoardLike(bno, id):
        // 게시글 번호(bno)와 사용자 ID(id)를 기반으로 좋아요를 데이터베이스에 추가합니다.
        // 성공 시, 좋아요가 정상적으로 추가됩니다.

        // 성공 메시지 추가
        map.put("code", "1"); // "1"은 성공 상태를 나타냅니다.
        map.put("msg", "해당 게시글에 좋아요 하셨습니다."); // 사용자에게 표시할 성공 메시지
      } catch (Exception e) {
        // 4. 예외 처리: 중복 좋아요 취소
        // boardService.insertBoardLike에서 예외가 발생하면,
        // 중복된 좋아요로 판단하여 기존 좋아요를 취소합니다.
        boardService.deleteBoardLike(bno, id);
        // boardService.deleteBoardLike(bno, id):
        // 게시글 번호(bno)와 사용자 ID(id)를 기반으로 기존 좋아요를 데이터베이스에서 제거합니다.

        // 취소 성공 메시지 추가
        map.put("code", "1"); // 취소도 성공으로 간주
        map.put("msg", "해당 게시글에 좋아요를 취소 하셨습니다."); // 사용자에게 표시할 취소 메시지
      }

      // 5. 현재 좋아요 수 반환
      map.put("count", boardService.getBoardLike(bno));
      // boardService.getBoardLike(bno):
      // 현재 게시글 번호(bno)에 해당하는 좋아요 수를 데이터베이스에서 조회합니다.
      // 반환값은 좋아요 개수로, JSON 응답에 포함됩니다.
    }

    // 6. 결과 데이터를 반환
    return map;
    // 반환된 map 객체는 @ResponseBody에 의해 JSON 형식으로 변환되어 클라이언트에 전달됩니다.
    // 예: {"code": "1", "msg": "해당 게시글에 좋아요 하셨습니다.", "count": 5}
  }

  // 게시물 싫어요 증가/감소 로직을 처리하는 메서드 -----------------------------------------------------------------------------------
  @ResponseBody
  @GetMapping("/hate/{bno}") // HTTP GET 요청이 "/hate/{bno}"로 들어오면 이 메서드가 실행됩니다.
  public Map<String, Object> boardHate(@PathVariable int bno, HttpSession session) {
    // @ResponseBody: 반환값을 JSON 형식으로 클라이언트에 반환합니다.
    // @GetMapping: HTTP GET 요청을 처리하며, URL 경로에서 {bno} 값을 매핑하여 메서드 매개변수로 전달합니다.
    // bno: 싫어요를 처리할 게시글 번호입니다.
    // session: 사용자 세션 정보를 담고 있습니다.

    // 결과 데이터를 저장할 맵 객체 생성
    Map<String, Object> map = new HashMap<String, Object>();
    // map: 결과 데이터를 저장할 Map 객체입니다.
    // 반환될 데이터는 JSON 형식으로 변환됩니다.
    // 예: {"code": 1, "msg": "성공 메시지", "count": 싫어요 수}

    // 1. 사용자 로그인 여부 확인
    if (session.getAttribute("user") == null) {
      // session.getAttribute("user"): 세션에서 "user"라는 속성을 가져옵니다.
      // null이면 사용자가 로그인하지 않은 상태로 판단됩니다.
      map.put("code", "2"); // "2"는 로그인 필요 상태를 나타냅니다.
      map.put("msg", "로그인 하셔야 이용하실수 있습니다."); // 사용자에게 표시할 메시지
    } else {
      // 2. 로그인한 사용자 ID 가져오기
      String id = (String) ((BoardMemberDTO) session.getAttribute("user")).getId();
      // session.getAttribute("user"): 세션에서 로그인한 사용자의 정보를 가져옵니다.
      // BoardMemberDTO: 사용자 정보를 저장하는 DTO 객체로, 여기서 사용자의 ID를 추출합니다.

      try {
        // 3. 싫어요 추가 로직
        boardService.insertBoardHate(bno, id);
        // boardService.insertBoardHate(bno, id):
        // 게시글 번호(bno)와 사용자 ID(id)를 기반으로 싫어요를 데이터베이스에 추가합니다.
        // 성공 시, 싫어요가 정상적으로 추가됩니다.

        // 성공 메시지 추가
        map.put("code", "1"); // "1"은 성공 상태를 나타냅니다.
        map.put("msg", "해당 게시글에 싫어요 하셨습니다."); // 사용자에게 표시할 성공 메시지
      } catch (Exception e) {
        // 4. 예외 처리: 중복 싫어요 취소
        // boardService.insertBoardHate에서 예외가 발생하면,
        // 중복된 싫어요로 판단하여 기존 싫어요를 취소합니다.
        boardService.deleteBoardHate(bno, id);
        // boardService.deleteBoardHate(bno, id):
        // 게시글 번호(bno)와 사용자 ID(id)를 기반으로 기존 싫어요를 데이터베이스에서 제거합니다.

        // 취소 성공 메시지 추가
        map.put("code", "1"); // 취소도 성공으로 간주
        map.put("msg", "해당 게시글에 싫어요를 취소 하셨습니다."); // 사용자에게 표시할 취소 메시지
      }

      // 5. 현재 싫어요 수 반환
      map.put("count", boardService.getBoardHate(bno));
      // boardService.getBoardHate(bno):
      // 현재 게시글 번호(bno)에 해당하는 싫어요 수를 데이터베이스에서 조회합니다.
      // 반환값은 싫어요 개수로, JSON 응답에 포함됩니다.
    }

    // 6. 결과 데이터를 반환
    return map;
    // 반환된 map 객체는 @ResponseBody에 의해 JSON 형식으로 변환되어 클라이언트에 전달됩니다.
    // 예: {"code": "1", "msg": "해당 게시글에 싫어요 하셨습니다.", "count": 3}
  }

  // 게시물 삭제 로직을 처리하는 메서드 -----------------------------------------------------------------------------------
  @GetMapping("/delete/{bno}") // HTTP GET 요청이 "/delete/{bno}"로 들어오면 이 메서드가 실행됩니다.
  public String boardDelete(@PathVariable int bno, HttpSession session, HttpServletResponse response) {
    // @GetMapping: HTTP GET 요청을 처리하며, URL 경로의 {bno}를 매핑하여 메서드 매개변수로 전달합니다.
    // bno: 삭제하려는 게시글 번호입니다.
    // session: 사용자 세션 정보를 확인하기 위한 객체입니다.
    // response: 클라이언트에게 HTTP 응답을 전송하기 위한 객체입니다.

    // 1. 사용자 로그인 상태와 작성자 본인 여부 확인
    if (session.getAttribute("user") != null // 사용자가 로그인한 상태인지 확인
        && ((BoardMemberDTO) session.getAttribute("user")).getId() // 세션에서 로그인 사용자의 ID 가져오기
            .equals(boardService.selectBoard(bno).getId())) { // 해당 게시글 작성자의 ID와 비교
      // session.getAttribute("user"): 세션에서 "user" 속성을 가져옵니다.
      // boardService.selectBoard(bno): 게시글 번호로 게시글 정보를 조회합니다.
      // 게시글 작성자와 로그인 사용자가 일치하면 삭제 가능

      // 1-1. 게시글 삭제 전 첨부파일이 존재하면 첨부파일을 먼저 삭제
      List<BoardFileDTO> fileList = boardService.getBoardFileList(bno);
      // boardService.getBoardFileList(bno): 해당 게시글에 첨부된 파일 목록을 조회

      fileList.forEach(file -> {
        // file.getFpath(): 파일 경로를 가져옵니다.
        new File(file.getFpath()).delete();
        // new File(file.getFpath()).delete(): 파일 경로에 해당하는 파일을 삭제
      });

      // 1-2. 게시글 데이터 삭제
      boardService.deleteBoard(bno);
      // boardService.deleteBoard(bno): 해당 게시글 번호(bno)를 기준으로 게시글 데이터를 삭제
    } else {
      // 1-3. 로그인하지 않았거나 본인이 작성한 게시글이 아닌 경우
      response.setContentType("text/html; charset=utf-8");
      // 클라이언트에게 HTML 형식의 응답을 전송하도록 응답의 콘텐츠 타입을 설정

      try {
        // JavaScript를 사용하여 경고 메시지와 함께 이전 페이지로 이동 처리
        response.getWriter().println(
            "<script>alert('해당 글 작성자만 삭제가 가능합니다.'); history.back();</script>");
        // alert(): 경고 메시지를 표시
        // history.back(): 이전 페이지로 돌아감
        return null; // 메서드 종료, 별도의 뷰 반환 없음
      } catch (Exception e) {
        e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력
      }
    }

    // 2. 게시글 삭제 완료 후 메인 페이지로 리다이렉트
    return "redirect:/main";
    // 삭제가 완료되면 메인 페이지로 리다이렉트
  }

  // 게시물 댓글 추가 로직을 처리하는 메서드 -----------------------------------------------------------------------------------
  @PostMapping("/comment") // HTTP POST 요청이 "/comment"로 들어오면 이 메서드가 실행됩니다.
  public String boardCommentWrite(BoardCommentDTO comment, HttpSession session, HttpServletResponse response) {
    // @PostMapping: POST 요청을 처리하며, 댓글 작성 요청을 처리하는 엔드포인트입니다.
    // comment: 클라이언트로부터 전달받은 댓글 정보를 담은 객체입니다.
    // session: 사용자 세션 정보를 확인하기 위한 객체입니다.
    // response: 클라이언트에 HTTP 응답을 전송하기 위한 객체입니다.

    // 1. 사용자 로그인 여부 확인
    if (session.getAttribute("user") == null) {
      // session.getAttribute("user"): 세션에서 "user"라는 속성을 가져옵니다.
      // null이면 사용자가 로그인하지 않은 상태로 간주합니다.

      response.setContentType("text/html; charset=utf-8");
      // 클라이언트에게 HTML 형식의 응답을 전송하도록 응답의 콘텐츠 타입을 설정합니다.

      try {
        // JavaScript를 사용하여 경고 메시지와 함께 로그인 페이지로 이동 처리
        response.getWriter().println(
            "<script>alert('로그인 하셔야 이용하실수 있습니다.'); location.href='/login/view';</script>");
        // alert(): 브라우저에 경고 메시지를 표시합니다.
        // location.href='/login/view': 로그인 페이지로 이동합니다.
      } catch (Exception e) {
        e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력합니다.
      }
      return null; // 처리 종료, 별도의 뷰 반환 없음
    }

    // 2. 로그인된 사용자의 ID 가져오기
    String id = ((BoardMemberDTO) session.getAttribute("user")).getId();
    // session.getAttribute("user"): 세션에서 로그인한 사용자의 정보를 가져옵니다.
    // BoardMemberDTO: 사용자 정보를 담고 있는 DTO 객체입니다.
    // ID는 현재 로그인된 사용자의 고유 식별자입니다.

    // 3. 댓글 정보 설정
    comment.setId(id);
    // comment 객체에 로그인된 사용자의 ID를 설정합니다.
    // 이 ID는 댓글 작성자를 식별하는 데 사용됩니다.

    // 4. 댓글 저장 로직 호출
    boardService.insertBoardComment(comment);
    // boardService.insertBoardComment(comment):
    // 서비스 계층을 호출하여 데이터베이스에 댓글 정보를 저장합니다.

    // 5. 댓글 작성 완료 후 게시글 상세 페이지로 리다이렉트
    return "redirect:/board/" + comment.getBno();
    // 댓글 작성이 완료된 후, 해당 게시글 상세 페이지로 리다이렉트합니다.
    // comment.getBno(): 댓글이 속한 게시글 번호를 가져옵니다.
    // "redirect:/board/{bno}": 리다이렉트 경로를 지정하여 클라이언트가 새로고침합니다.
  }

  // 댓글 갯수 5개씩 증가 더보기 로직을 처리하는 메서드 -------------------------------------------------------------------
  @ResponseBody
  @GetMapping("/comment/{bno}") // HTTP GET 요청을 처리하며, URL 경로의 {bno}를 매핑하여 게시글 번호로 사용
  public List<BoardCommentDTO> getMethodName(@PathVariable int bno, @RequestParam int start) {
    // @ResponseBody: 반환값(List<BoardCommentDTO>)을 JSON 형식으로 직렬화하여 클라이언트에 전달합니다.
    // @GetMapping("/comment/{bno}"): /comment/{bno} 경로의 GET 요청을 처리하며, {bno}는 게시글
    // 번호로 매핑됩니다.
    // @PathVariable: URL 경로의 {bno} 값을 추출하여 메서드 매개변수 bno에 매핑합니다.
    // @RequestParam: 쿼리 파라미터(start)의 값을 추출하여 매개변수 start에 매핑합니다.

    // 1. 서비스 계층을 호출하여 댓글 리스트를 조회합니다.
    List<BoardCommentDTO> commentList = boardService.getCommentList(bno, start);
    // boardService.getCommentList(bno, start):
    // - bno: 특정 게시글의 댓글만 조회하기 위해 게시글 번호를 전달합니다.
    // - start: 댓글 조회를 시작할 인덱스를 전달합니다.
    // - 이 메서드는 MyBatis 쿼리와 연결되어 데이터베이스에서 댓글 데이터를 조회합니다.
    // - 데이터베이스 쿼리는 start부터 특정 갯수(예: 5개)만 조회하도록 설정됩니다.

    // 2. 조회된 댓글 리스트를 반환합니다.
    return commentList;
    // 반환값: List<BoardCommentDTO>는 댓글 정보를 담은 DTO 객체 리스트입니다.
    // 클라이언트는 이 JSON 응답을 파싱하여 화면에 댓글을 추가로 표시합니다.
  }

  // 댓글 좋아요 로직을 처리하는 메서드 -------------------------------------------------------------------
  @GetMapping("/comment/like/{cno}") // HTTP GET 요청을 처리하며 URL 경로의 {cno}를 댓글 번호로 사용
  @ResponseBody // 메서드의 반환값을 JSON 형식으로 클라이언트에 반환
  public Map<String, Object> boardCommentLike(@PathVariable int cno, HttpSession session) {
    // 결과 데이터를 저장할 Map 객체 생성
    Map<String, Object> map = new HashMap<String, Object>();

    /**
     * 이 메서드는 클라이언트로부터 댓글 좋아요 요청을 처리합니다.
     * 
     * 1. REST API 방식의 컨트롤러 메서드로, HTTP 요청과 매핑됩니다.
     * 2. 반환값은 JSON 형식으로 클라이언트에 전달됩니다.
     * 3. 이 메서드는 세션에서 사용자 로그인 여부를 확인하고,
     * - 사용자가 로그인 상태일 경우, 댓글에 좋아요를 추가하거나 중복 좋아요를 취소합니다.
     * - 로그인하지 않은 상태라면, 클라이언트에 경고 메시지를 반환합니다.
     */

    // 1. 사용자 로그인 여부 확인
    if (session.getAttribute("user") == null) {
      // 세션에 사용자 정보가 없을 경우, 즉 로그인이 안 된 상태
      map.put("code", 2); // 2는 "로그인 필요" 상태 코드를 나타냅니다.
      map.put("msg", "로그인 하셔야 이용하실수 있습니다."); // 클라이언트에 반환할 메시지
    } else {
      // 2. 로그인한 사용자 정보 가져오기
      String id = ((BoardMemberDTO) session.getAttribute("user")).getId(); // 세션에서 사용자 ID를 가져옵니다.

      /**
       * BoardMemberDTO는 데이터 전송 객체(DTO)로, 사용자의 정보를 포함하는 객체입니다.
       * 여기서 session.getAttribute("user")는 로그인한 사용자의 정보를 가져옵니다.
       * 
       * id는 사용자가 댓글에 좋아요를 추가하거나 취소할 때 필요한 유일한 사용자 식별 값입니다.
       */

      try {
        // 3. 좋아요 추가 로직
        boardService.insertBoardCommentLike(cno, id); // 댓글 좋아요를 데이터베이스에 추가
        map.put("code", 1); // 1은 "성공" 상태 코드를 나타냅니다.
        map.put("msg", "해당 댓글에 좋아요 하셨습니다."); // 성공 메시지 반환
      } catch (Exception e) {
        // 4. 좋아요 중복 시 취소 로직
        boardService.deleteBoardCommentLike(cno, id); // 댓글 좋아요를 데이터베이스에서 제거
        map.put("code", 1); // 중복 좋아요 취소도 성공으로 간주
        map.put("msg", "해당 댓글에 좋아요를 취소 하셨습니다."); // 취소 메시지 반환
      }

      // 5. 댓글 좋아요 수 반환
      map.put("count", boardService.selectCommentLikeCount(cno));
      /**
       * 좋아요 수를 가져오기 위해 서비스 계층의 selectCommentLikeCount 메서드를 호출합니다.
       * 이 메서드는 데이터베이스에서 해당 댓글(cno)의 좋아요 개수를 조회하여 반환합니다.
       * 반환값은 int 타입의 숫자이며, 클라이언트에 JSON 응답으로 포함됩니다.
       */
    }

    // 6. 결과 데이터를 JSON 형식으로 반환
    return map;

    /**
     * 반환값은 Map<String, Object> 객체입니다.
     * - key는 String 타입으로, 클라이언트가 응답 데이터를 이해할 수 있도록 정의됩니다.
     * 예) "code", "msg", "count" 등
     * - value는 Object 타입으로, 반환할 데이터 값입니다.
     * 예) 상태 코드(1, 2), 메시지, 좋아요 개수 등
     * 
     * 이 메서드는 @ResponseBody 어노테이션에 의해 Map 객체를 JSON 형식으로 변환하여 클라이언트에 반환합니다.
     * 예: {"code":1, "msg":"해당 댓글에 좋아요 하셨습니다.", "count":5}
     */
  }

  // 댓글 싫어요 로직 처리하는 메서드 -------------------------------------------------------------------
  @GetMapping("/comment/hate/{cno}") // HTTP GET 요청을 처리하며, URL 경로의 {cno}를 댓글 번호로 사용
  @ResponseBody // 메서드의 반환값을 JSON 형식으로 클라이언트에 반환
  public Map<String, Object> boardCommentHate(@PathVariable int cno, HttpSession session) {
    // 결과 데이터를 저장할 Map 객체 생성
    Map<String, Object> map = new HashMap<String, Object>();

    /**
     * 이 메서드는 댓글에 대한 "싫어요" 요청을 처리하는 RESTful API 엔드포인트입니다.
     * 
     * 1. URL 경로의 댓글 번호({cno})를 매핑하여 특정 댓글에 싫어요를 추가하거나 취소합니다.
     * 2. 사용자의 로그인 여부를 확인한 뒤,
     * - 로그인된 사용자는 댓글 싫어요를 추가하거나, 이미 싫어요를 누른 경우 싫어요를 취소합니다.
     * - 비로그인 사용자는 경고 메시지를 반환합니다.
     * 3. 최종적으로 싫어요 개수와 처리 상태 메시지를 JSON 형식으로 반환합니다.
     */

    // 1. 사용자 로그인 여부 확인
    if (session.getAttribute("user") == null) {
      // 사용자가 로그인하지 않은 경우 처리
      map.put("code", 2); // 상태 코드 2는 "로그인 필요"를 나타냅니다.
      map.put("msg", "로그인 하셔야 이용하실수 있습니다."); // 클라이언트에 반환할 경고 메시지
    } else {
      // 2. 로그인된 사용자의 ID 가져오기
      String id = ((BoardMemberDTO) session.getAttribute("user")).getId();
      /**
       * `BoardMemberDTO`는 사용자 정보를 담은 데이터 전송 객체(DTO)입니다.
       * - 세션에서 "user" 객체를 가져와 현재 로그인된 사용자의 정보를 얻습니다.
       * - 사용자 ID는 댓글 싫어요를 추가하거나 취소할 때 고유 식별자로 사용됩니다.
       */

      try {
        // 3. 댓글 싫어요 추가 로직
        boardService.insertBoardCommentHate(cno, id); // 데이터베이스에 댓글 싫어요 추가
        map.put("code", 1); // 상태 코드 1은 성공을 나타냅니다.
        map.put("msg", "해당 댓글에 싫어요 하셨습니다."); // 싫어요 추가 성공 메시지 반환
      } catch (Exception e) {
        // 4. 댓글 싫어요 중복 시 처리 로직
        boardService.deleteBoardCommentHate(cno, id); // 기존 싫어요를 데이터베이스에서 제거
        map.put("code", 1); // 상태 코드 1은 성공(싫어요 취소)으로 간주
        map.put("msg", "해당 댓글에 싫어요를 취소 하셨습니다."); // 싫어요 취소 성공 메시지 반환
      }

      // 5. 댓글의 싫어요 개수 조회
      map.put("count", boardService.selectCommentHateCount(cno));
      /**
       * `selectCommentHateCount`는 댓글 번호(cno)를 기준으로 싫어요 개수를 데이터베이스에서 조회합니다.
       * 반환값은 `int` 형식으로, 댓글에 누적된 싫어요 수를 나타냅니다.
       * 이 값은 클라이언트에 JSON 형식으로 전달됩니다.
       */
    }

    // 6. 결과를 JSON 형식으로 반환
    return map;

    /**
     * 반환값은 `Map<String, Object>` 형태입니다.
     * - key: 결과 데이터를 식별할 수 있는 문자열 값("code", "msg", "count" 등)
     * - value: 결과 데이터를 담은 값(Object). 예: 상태 코드(Integer), 메시지(String), 싫어요
     * 개수(Integer)
     * 
     * @ResponseBody 어노테이션 덕분에 이 Map 객체는 자동으로 JSON 형식으로 변환되어 클라이언트로 전송됩니다.
     *               예시 응답:
     *               {"code":1, "msg":"해당 댓글에 싫어요 하셨습니다.", "count":3}
     */
  }

  // 댓글 삭제 로직 처리 메서드 -------------------------------------------------------------------
  @GetMapping("/comment/delete/{cno}") // HTTP GET 요청으로 "/comment/delete/{cno}" URL에 매핑
  public String commentDelete(@PathVariable int cno, HttpSession session, HttpServletResponse response) {

    // 1. 특정 댓글 번호(cno)를 기준으로 댓글 정보를 조회
    BoardCommentDTO comment = boardService.selectComment(cno);
    /**
     * BoardCommentDTO는 댓글 정보를 담은 데이터 전송 객체(DTO)입니다.
     * - `selectComment(cno)`: 댓글 번호(cno)를 기준으로 댓글 정보를 데이터베이스에서 조회합니다.
     * - 반환값은 댓글의 작성자 ID, 댓글 내용, 게시글 번호 등 댓글 관련 데이터를 포함한 객체입니다.
     * 예: BoardCommentDTO { id="작성자ID", content="댓글 내용", bno=123 }
     */

    // 2. 로그인 상태 및 댓글 작성자 확인
    if (session.getAttribute("user") != null // 세션에 사용자가 로그인 상태인지 확인
        && ((BoardMemberDTO) session.getAttribute("user")).getId().equals(comment.getId())) {
      /**
       * - 세션에서 "user" 속성을 가져와 로그인된 사용자 정보를 확인합니다.
       * - `BoardMemberDTO`는 사용자 정보를 담은 DTO 객체로, 로그인된 사용자의 ID를 가져옵니다.
       * - 댓글 작성자의 ID(`comment.getId()`)와 현재 로그인 사용자의 ID를 비교하여 동일한지 확인합니다.
       * - 동일하면 본인이 작성한 댓글이므로 삭제를 허용합니다.
       */

      // 3. 댓글 삭제 로직 실행
      boardService.deleteBoardComment(cno);
      /**
       * `deleteBoardComment(cno)`는 데이터베이스에서 해당 댓글 번호(cno)를 삭제합니다.
       * 댓글 삭제는 게시글 번호와 관계없이 특정 댓글을 고유 번호로 식별하여 처리합니다.
       */
    } else {
      // 4. 댓글 작성자가 아닌 경우 경고 메시지 반환
      response.setContentType("text/html; charset=utf-8");
      /**
       * - 클라이언트로 HTML 형식의 응답을 보내기 위해 응답의 콘텐츠 타입을 설정합니다.
       * - UTF-8 인코딩을 지정하여 한글 메시지가 올바르게 표시되도록 설정합니다.
       */

      try {
        // JavaScript 경고 메시지를 클라이언트에게 전달
        response.getWriter().println(
            "<script>alert('해당 글 작성자만 삭제가 가능합니다..'); history.back();</script>");
        /**
         * - `response.getWriter().println()`: 클라이언트로 HTML 및 JavaScript 코드를 전송합니다.
         * - alert(): 브라우저에 경고 메시지를 표시합니다.
         * - history.back(): 브라우저의 이전 페이지로 돌아갑니다.
         */
        return null; // 메서드 실행을 종료하고 별도의 뷰를 반환하지 않음
      } catch (Exception e) {
        // 예외 발생 시 로그 출력
        e.printStackTrace();
      }
      return null; // 예외 발생 시에도 클라이언트에 별도의 뷰를 반환하지 않음
    }

    // 5. 댓글 삭제 후 게시글 상세 페이지로 리다이렉트
    return "redirect:/board/" + comment.getBno();
    /**
     * - 댓글 삭제가 완료되면 해당 댓글이 속한 게시글의 상세 페이지로 리다이렉트합니다.
     * - `comment.getBno()`: 삭제된 댓글이 속한 게시글 번호를 가져옵니다.
     * - "redirect:/board/{bno}": 스프링에서 리다이렉트 경로를 지정하는 형식입니다.
     */
  }

  // 댓글 수정 로직 처리 메서드 -------------------------------------------------------------------
  @ResponseBody
  @PatchMapping("/comment") // HTTP PATCH 요청을 "/comment" 경로와 매핑
  public Map<String, Object> boardCommentUpdate(@RequestBody Map<String, String> body, HttpSession session) {
    // 1. 반환할 데이터를 저장할 맵 객체 생성
    Map<String, Object> map = new HashMap<String, Object>();
    /**
     * - Map<String, Object>: 수정 결과를 JSON 형식으로 반환하기 위해 사용됩니다.
     * - 반환값의 키-값 구조:
     * - code: 작업 성공 여부를 나타내는 상태 코드 (1: 성공, 2: 실패)
     * - msg: 처리 결과 메시지
     */

    // 2. 수정하려는 댓글의 정보를 데이터베이스에서 조회
    BoardCommentDTO comment = boardService.selectComment(Integer.parseInt(body.get("cno")));
    /**
     * - `body.get("cno")`: 요청 데이터로 전달된 댓글 번호를 가져옵니다.
     * - `Integer.parseInt()`: 댓글 번호를 문자열에서 정수로 변환.
     * - `boardService.selectComment(cno)`: 댓글 번호를 기준으로 데이터베이스에서 댓글 정보를 조회.
     * - 반환값: 댓글 정보(BoardCommentDTO 객체), 댓글 작성자 ID, 댓글 내용 등을 포함.
     */

    // 3. 세션 확인 및 댓글 작성자 검증
    if (session.getAttribute("user") != null // 세션에 사용자가 로그인 상태인지 확인
        && ((BoardMemberDTO) session.getAttribute("user")).getId().equals(comment.getId())) {
      /**
       * - `session.getAttribute("user")`: 로그인된 사용자 정보를 가져옴.
       * - `BoardMemberDTO`: 사용자 정보를 담은 DTO 객체.
       * - `comment.getId()`: 댓글 작성자의 ID를 가져옴.
       * - 조건: 로그인된 사용자와 댓글 작성자가 동일한 경우에만 댓글을 수정할 수 있음.
       */

      // 4. 댓글 내용 업데이트
      comment.setContent(body.get("content")); // 요청 데이터에서 새로운 댓글 내용을 가져와 설정
      boardService.updateBoardComment(comment); // 댓글 정보를 업데이트하는 서비스 호출
      /**
       * - `setContent()`: 댓글 내용 수정.
       * - `boardService.updateBoardComment(comment)`: 수정된 댓글 정보를 데이터베이스에 업데이트.
       */

      // 5. 수정 성공 응답 생성
      map.put("code", 1); // 상태 코드 1: 수정 성공
      map.put("msg", "해당 댓글 수정 완료"); // 성공 메시지 반환
    } else {
      // 6. 댓글 작성자가 아닌 경우, 수정 불가 처리
      map.put("code", 2); // 상태 코드 2: 수정 실패
      map.put("msg", "해당 댓글 작성자만 수정이 가능합니다."); // 실패 메시지 반환
    }

    // 7. 클라이언트로 결과 반환 (JSON 형식)
    return map;
    /**
     * - @ResponseBody: 반환값(Map 객체)을 JSON 형식으로 직렬화하여 클라이언트에 응답.
     * - 클라이언트는 JSON 데이터를 파싱하여 결과를 화면에 표시하거나 로직을 처리.
     */
  }

  // 게시글 작성 페이지 이동 메서드 -------------------------------------------------------------------
  @GetMapping("/write/view") // HTTP GET 요청으로 "/write/view" 경로를 처리
  public String boardView() {
    // 게시글 작성 페이지로 이동
    return "board_write_view"; // View 이름 반환 (HTML 파일)
  }

  // 게시글 작성 로직 처리 메서드 ------------------------------------------------------------------- 수정전
  // @PostMapping("/write")
  // public String boardWrite(BoardDTO board, HttpSession session,
  // @RequestParam(value = "file", required = false) MultipartFile[] files) throws
  // IllegalStateException, IOException {
  // //1. 사용자가 작성한 게시글 제목, 내용, 파일 받아옴
  // //2. 작성자는 세션에서 아이디만 가져옴
  // String id = ((BoardMemberDTO)session.getAttribute("user")).getId();
  // board.setId(id);
  // //3. 게시글 새번호 받아옴
  // int bno = boardService.selectBoardNo();
  // board.setBno(bno);
  // // System.out.println(bno);
  // //4. 파일 업로드
  // List<BoardFileDTO> fileList = new ArrayList<>();
  // File root = new File("C:\\fileupload");
  // //해당 경로가 있는지 체크, 없으면 해당 경로를 생성
  // if(!root.exists()){
  // root.mkdirs();
  // }
  // for(MultipartFile file : files){
  // //파일 업로드 전에 검사
  // if(file.isEmpty()){
  // continue;
  // }
  // //업로드한 파일명
  // String fileName = UUID.randomUUID()+ "_" + file.getOriginalFilename(); //
  // 파일명앞에 UUID를 추가
  // //파일 저장할 경로 완성
  // String filePath = root + File.separator + fileName;
  // //실제 파일 저장 부분
  // file.transferTo(new File(filePath));
  // BoardFileDTO fileDTO = new BoardFileDTO();
  // fileDTO.setBno(bno);
  // fileDTO.setFpath(filePath);
  // fileList.add(fileDTO);
  // }
  // //5. 게시글 데이터베이스에 추가
  // boardService.insertBoard(board, fileList);

  // return "redirect:/board/" + board.getBno();
  // }

  // 게시글 작성 로직 처리 메서드 -------------------------------------------------------------------
  @PostMapping("/write") // HTTP POST 요청으로 "/write" 경로를 처리
  public String boardWrite(BoardDTO board, HttpSession session,
      @RequestParam(value = "file", required = false) MultipartFile[] files) throws IllegalStateException, IOException {
    // 1. 세션에서 작성자 ID 가져오기
    String id = ((BoardMemberDTO) session.getAttribute("user")).getId(); // 로그인한 사용자 정보에서 ID 추출
    board.setId(id); // 게시글 작성자 ID 설정

    // 2. 파일 업로드 처리
    List<BoardFileDTO> fileList = new ArrayList<>(); // 업로드 파일 정보를 저장할 리스트 생성
    File root = new File("C:\\fileupload"); // 업로드 파일 저장 디렉터리 경로 지정
    if (!root.exists()) { // 경로가 존재하지 않으면
      root.mkdirs(); // 디렉터리 생성
    }

    for (MultipartFile file : files) { // 업로드된 파일들을 반복 처리
      if (file.isEmpty()) { // 파일이 비어있으면 건너뜀
        continue;
      }
      String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename(); // 고유 파일 이름 생성
      String filePath = root + File.separator + fileName; // 저장할 파일 경로 생성
      file.transferTo(new File(filePath)); // 실제 파일 저장
      BoardFileDTO fileDTO = new BoardFileDTO(); // 파일 정보 DTO 생성
      fileDTO.setFpath(filePath); // 파일 경로 설정
      fileList.add(fileDTO); // 파일 정보 리스트에 추가
    }

    // 3. 서비스 계층에 게시글과 파일 정보 전달
    boardService.insertBoard(board, fileList);

    // 4. 작성된 게시글 상세 페이지로 리다이렉트
    return "redirect:/board/" + board.getBno();
  }

  // 파일 다운로드 처리 메서드 -------------------------------------------------------------------
  @GetMapping("/download/{fno}") // HTTP GET 요청으로 "/download/{fno}" 경로를 처리
  public void fileDownload(@PathVariable int fno, HttpServletResponse response)
      throws FileNotFoundException, IOException {
    // 1. 파일 경로 정보 가져오기
    String filePath = boardService.selectFilePath(fno); // 파일 번호로 파일 경로 조회

    // 2. 파일 다운로드 설정
    File file = new File(filePath); // 다운로드할 파일 객체 생성
    String encodingFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8); // 파일명 인코딩
    response.setHeader("Content-Disposition", "attachment;fileName=" + encodingFileName); // 헤더 설정
    response.setHeader("Content-Transfer-Encoding", "binary"); // 바이너리 데이터 전송 설정
    response.setContentLength((int) file.length()); // 파일 크기 설정

    // 3. 파일 전송
    try (FileInputStream fis = new FileInputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
      byte[] buffer = new byte[1024 * 1024]; // 버퍼 크기 설정
      while (true) {
        int count = fis.read(buffer); // 파일 읽기
        if (count == -1) { // EOF 확인
          break;
        }
        bos.write(buffer, 0, count); // 클라이언트에 데이터 전송
        bos.flush(); // 데이터 플러시
      }
    }
  }

  // 게시글 수정 페이지 이동 로직 처리 메서드 -------------------------------------------------------------------
  @GetMapping("/update/view") // HTTP GET 요청으로 "/update/view" 경로를 처리
  public ModelAndView boardUpdateView(int bno, ModelAndView view) {
    BoardDTO board = boardService.selectBoard(bno); // 게시글 번호로 게시글 정보 조회
    view.addObject("board", board); // 게시글 정보 뷰에 추가
    view.setViewName("board_update_view"); // 수정 페이지로 이동
    return view; // 수정 페이지 뷰 반환
  }

  // 게시글 수정 로직 처리 메서드 -------------------------------------------------------------------
  @PostMapping("/update") // HTTP POST 요청으로 "/update" 경로를 처리
  public String updateBoard(BoardDTO board) {
    boardService.updateBoard(board); // 서비스 계층에서 게시글 업데이트 실행
    return "redirect:/board/" + board.getBno(); // 수정된 게시글 상세 페이지로 리다이렉트
  }

}
