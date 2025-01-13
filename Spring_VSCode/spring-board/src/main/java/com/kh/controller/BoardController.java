package com.kh.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.dto.BoardCommentDTO;
import com.kh.dto.BoardDTO;
import com.kh.dto.BoardMemberDTO;
import com.kh.service.BoardService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/board") // 컨트롤러의 기본 경로를 "/board"로 설정합니다.
@Controller // Spring MVC에서 이 클래스를 컨트롤러로 사용하겠다는 선언입니다.
public class BoardController {

  private BoardService boardService; // 게시글과 관련된 비즈니스 로직을 처리하는 서비스 클래스 의존성

  // BoardController의 생성자
  // @Autowired 없이 생성자 주입을 통해 boardService를 주입받습니다.
  public BoardController(BoardService boardService) {
    this.boardService = boardService; // 생성자를 통해 서비스 객체를 초기화합니다.
  }

  // 컨트롤러 내 주요 메서드들은 아래에 정의되어 있습니다.
  // 각각의 메서드는 특정 HTTP 요청(GET)과 매핑되며, 요청 데이터를 처리하고 결과를 반환합니다.

  // 특정 게시글의 상세 정보를 조회하고 조회수를 증가시키는 메서드 -----------------------------------------------------------------------------------
  @GetMapping("/{bno}") // HTTP GET 요청을 처리하며, URL 경로의 {bno}를 매핑하여 게시글 번호로 사용
  public ModelAndView boardDetail(@PathVariable int bno, ModelAndView view, HttpSession session) {

    // 1. 세션에서 'visited'라는 이름으로 저장된 HashSet<Integer>를 가져옵니다.
    // - 'visited'는 사용자가 조회한 게시글 번호(bno)를 저장하는 역할을 합니다.
    // - 세션은 각 사용자별로 관리되므로, 다른 사용자의 조회 기록과 독립적으로 동작합니다.
    @SuppressWarnings("unchecked") // @SuppressWarnings("unchecked") : 제네릭(Generics)과 관련된 **미확인 형변환 경고(unchecked
                                   // cast warning)**를 억제하도록 지정
    HashSet<Integer> visited = (HashSet<Integer>) session.getAttribute("visited");

    // 2. 세션에 'visited'가 없는 경우, 즉 사용자가 아직 게시글 조회 기록을 저장한 적이 없을 경우
    // 새로운 HashSet<Integer>를 생성하여 초기화합니다.
    if (visited == null) {
      visited = new HashSet<Integer>(); // 새로운 HashSet 객체 생성
      session.setAttribute("visited", visited); // 세션에 'visited' 이름으로 저장
    }

    // 3. 게시글 번호(bno)를 HashSet에 추가합니다.
    // - HashSet의 add() 메서드는 중복된 값이 추가되지 않도록 보장합니다.
    // - bno가 HashSet에 없으면 true를 반환하고, 이미 존재하면 false를 반환합니다.
    if (visited.add(bno)) {
      // 4. 게시글 번호가 HashSet에 새로 추가된 경우에만 실행되는 블록
      // - 이 조건은 사용자가 해당 게시글을 처음 조회한 경우에만 참이 됩니다.
      // - 'boardService.updateBoardCount(bno)'는 게시글의 조회수를 데이터베이스에서 1 증가시킵니다.
      boardService.updateBoardCount(bno);
    }

    // 5. 서비스 계층을 호출하여 게시글 상세 정보를 가져옵니다.
    // - selectBoard(bno)는 게시글 번호를 기준으로 게시글의 세부 정보를 조회하여 BoardDTO 객체로 반환합니다.
    BoardDTO board = boardService.selectBoard(bno);

    // 6. 서비스 계층을 호출하여 댓글 리스트를 가져옵니다.
    // - getCommentList(bno)는 해당 게시글 번호에 달린 댓글 목록을 조회하여 List<BoardCommentDTO>로
    // 반환합니다.
    List<BoardCommentDTO> commentList = boardService.getCommentList(bno, 1);

    // 7. 조회된 게시글 상세 정보와 댓글 리스트를 뷰에 추가합니다.
    // - 뷰에서 'board'라는 이름으로 게시글 정보를 접근할 수 있도록 설정
    view.addObject("board", board);
    // - 뷰에서 'commentList'라는 이름으로 댓글 리스트를 접근할 수 있도록 설정
    view.addObject("commentList", commentList);

    // 8. 반환할 뷰의 이름을 설정합니다.
    // - "board_view"는 클라이언트에 반환될 HTML 파일의 이름입니다.
    view.setViewName("board_view");

    // 9. 클라이언트로 데이터를 포함한 뷰를 반환합니다.
    // - ModelAndView 객체는 뷰 이름과 뷰에 전달할 데이터를 함께 관리합니다.
    return view;
  }

  // 게시물 좋아요 증가/감소 로직을 처리하는 메서드 -----------------------------------------------------------------------------------
  @ResponseBody
  @GetMapping("/like/{bno}") // HTTP GET 요청을 처리하며, URL 경로에 포함된 {bno}를 매핑합니다.
  public Map<String, Object> boardLike(@PathVariable int bno, HttpSession session) {
    // 결과를 반환할 데이터를 저장할 맵 객체 생성
    Map<String, Object> map = new HashMap<String, Object>();

    // 1. 사용자 로그인 여부 확인
    // 세션에서 "user" 속성을 가져옵니다. "user" 속성이 null이면 로그인이 되어 있지 않다고 판단합니다.
    if (session.getAttribute("user") == null) {
      // 사용자가 로그인을 하지 않은 상태라면 다음 데이터를 반환합니다:
      map.put("code", "2"); // 코드 2는 "로그인 필요"를 나타냄
      map.put("msg", "로그인 하셔야 이용하실수 있습니다."); // 메시지에 로그인 필요 메시지 추가
    } else {
      // 2. 사용자가 로그인한 상태라면, 세션에서 사용자 ID를 가져옵니다.
      String id = (String) ((BoardMemberDTO) session.getAttribute("user")).getId();

      try {
        // 3. 좋아요 추가 로직
        // 게시글 번호(bno)와 사용자 ID를 이용해 좋아요를 추가합니다.
        boardService.insertBoardLike(bno, id);

        // 성공 시 반환할 데이터
        map.put("code", "1"); // 코드 1은 성공을 나타냄
        map.put("msg", "해당 게시글에 좋아요 하셨습니다."); // 성공 메시지 추가
      } catch (Exception e) {
        // 4. 좋아요 추가 시 예외 발생 시 처리 (중복 좋아요로 인한 경우)
        // 이미 좋아요가 존재하면 Exception이 발생하므로, 예외가 발생하면 좋아요를 취소합니다.
        boardService.deleteBoardLike(bno, id);

        // 취소 성공 시 반환할 데이터
        map.put("code", "1"); // 코드 1은 성공을 나타냄 (취소도 성공으로 간주)
        map.put("msg", "해당 게시글에 좋아요를 취소 하셨습니다."); // 취소 메시지 추가
      }

      // 5. 좋아요 수 반환
      // 현재 게시글의 좋아요 수를 가져와 맵에 추가합니다.
      map.put("count", boardService.getBoardLike(bno));
    }

    // 6. 결과 데이터를 반환
    // 결과 맵은 클라이언트에 JSON 형식으로 반환됩니다.
    return map;
  }

  // 게시물 싫어요 증가/감소 로직을 처리하는 메서드 -----------------------------------------------------------------------------------
  @ResponseBody
  @GetMapping("/hate/{bno}") // HTTP GET 요청을 처리하며, URL 경로의 {bno}를 변수로 받습니다.
  public Map<String, Object> boardHate(@PathVariable int bno, HttpSession session) {
    // 클라이언트에 반환할 데이터를 저장할 맵 객체 생성
    Map<String, Object> map = new HashMap<String, Object>();

    // 1. 로그인 여부 확인
    // 세션에서 "user" 속성을 가져옵니다. "user"가 null이면 로그인되지 않은 상태입니다.
    if (session.getAttribute("user") == null) {
      // 사용자가 로그인하지 않은 경우
      map.put("code", "2"); // 코드 2는 "로그인 필요"를 나타냅니다.
      map.put("msg", "로그인 하셔야 이용하실수 있습니다."); // 오류 메시지 반환
    } else {
      // 2. 로그인한 사용자의 ID 가져오기
      // 세션에서 "user" 객체를 가져와 `BoardMemberDTO`로 변환 후 사용자 ID를 얻습니다.
      String id = (String) ((BoardMemberDTO) session.getAttribute("user")).getId();

      try {
        // 3. 싫어요 추가 로직
        // 게시글 번호(bno)와 사용자 ID를 이용해 싫어요를 추가합니다.
        boardService.insertBoardHate(bno, id);

        // 싫어요 성공 메시지 반환
        map.put("code", "1"); // 코드 1은 성공을 나타냅니다.
        map.put("msg", "해당 게시글에 싫어요 하셨습니다.");
      } catch (Exception e) {
        // 4. 싫어요 추가 실패 처리 (이미 싫어요를 누른 경우)
        // `boardService.insertBoardHate`에서 예외가 발생하면 싫어요 취소 로직 실행
        boardService.deleteBoardHate(bno, id);

        // 싫어요 취소 성공 메시지 반환
        map.put("code", "1"); // 코드 1은 성공을 나타냅니다.
        map.put("msg", "해당 게시글에 싫어요를 취소 하셨습니다.");
      }

      // 5. 싫어요 개수 반환
      // 현재 게시글의 싫어요 수를 가져와 클라이언트에 반환합니다.
      map.put("count", boardService.getBoardHate(bno));
    }

    // 6. 결과 반환
    // 클라이언트에 JSON 형식으로 결과 데이터를 반환합니다.
    return map;
  }

  // 게시물 삭제 로직을 처리하는 메서드 -----------------------------------------------------------------------------------
  // HTTP GET 요청으로 /board/delete/{bno} 경로가 호출되었을 때 실행되는 메서드입니다.
  // 게시글 삭제를 처리하며, 작성자 본인만 삭제할 수 있도록 권한을 확인합니다.
  @GetMapping("/delete/{bno}")
  public String boardDelete(@PathVariable int bno, HttpSession session, HttpServletResponse response) {

    // 1. 사용자 로그인 상태와 작성자 본인 여부 확인
    // 세션에서 "user" 속성을 확인하여 사용자가 로그인 상태인지 확인합니다.
    // 또한, 세션에 저장된 사용자 ID와 삭제하려는 게시글의 작성자 ID를 비교하여 본인 여부를 확인합니다.
    if (session.getAttribute("user") != null &&
        ((BoardMemberDTO) session.getAttribute("user")).getId().equals(boardService.selectBoard(bno).getId())) {

      // 1-1. 사용자가 로그인 상태이고 본인이 작성한 게시글인 경우
      // 서비스 계층을 호출하여 게시글을 삭제합니다.
      boardService.deleteBoard(bno);

    } else {
      // 1-2. 사용자가 로그인하지 않았거나 본인이 작성한 게시글이 아닌 경우

      // 클라이언트에 HTML 응답을 보내기 위해 응답의 콘텐츠 타입을 설정합니다.
      response.setContentType("text/html; charset=utf-8");

      try {
        // 자바스크립트를 사용하여 경고 메시지를 클라이언트에게 표시하고 이전 페이지로 이동시킵니다.
        response.getWriter().println(
            "<script>alert('해당 글 작성자만 삭제가 가능합니다.'); history.back();</script>");
        return null; // 메서드 실행을 종료하고, 클라이언트에게 별도의 뷰를 반환하지 않습니다.
      } catch (Exception e) {
        // 예외 발생 시 에러 메시지를 출력합니다.
        e.printStackTrace();
      }
    }

    // 2. 게시글 삭제 완료 후 리다이렉트 처리
    // 사용자를 메인 페이지로 리다이렉트 시킵니다.
    return "redirect:/main";
  }

  // 게시물 댓글 추가 로직을 처리하는 메서드 -----------------------------------------------------------------------------------
  @PostMapping("/comment")
  // // 댓글 작성 요청을 처리하는 POST 매핑
  public String boardCommentWrite(BoardCommentDTO comment, HttpSession session, HttpServletResponse response) {
    // 1. 사용자 로그인 여부 확인
    // 세션에서 "user" 속성을 확인하여 로그인 상태를 판별
    if (session.getAttribute("user") == null) {
      // 로그인되지 않은 상태라면 클라이언트에 경고 메시지를 표시하고 로그인 페이지로 이동
      response.setContentType("text/html; charset=utf-8"); // 응답 콘텐츠 타입 설정
      try {
        // 자바스크립트를 사용하여 경고 메시지와 함께 페이지 이동 처리
        response.getWriter().println(
            "<script>alert('로그인 하셔야 이용하실수 있습니다.'); location.href='/login/view';</script>");
      } catch (Exception e) {
        e.printStackTrace(); // 예외 발생 시 에러 로그 출력
      }
      return null; // 처리 종료
    }

    // 2. 로그인된 사용자의 ID 가져오기
    // 세션에서 "user" 객체를 가져와 사용자 ID를 추출
    String id = ((BoardMemberDTO) session.getAttribute("user")).getId();

    // 3. 댓글 정보 설정
    // 전달받은 댓글 객체(comment)에 사용자 ID 설정
    comment.setId(id);

    // 4. 댓글 저장 로직 호출
    // 서비스 계층(boardService)을 호출하여 댓글을 데이터베이스에 저장
    boardService.insertBoardComment(comment);

    // 5. 댓글 작성 완료 후 게시글 상세 페이지로 리다이렉트
    // 댓글 작성과 관련된 게시글 번호(comment.getBno())를 사용하여 리다이렉트 경로 설정
    return "redirect:/board/" + comment.getBno();
  }

  // 댓글 갯수 5개씩 증가 더보기 로직을 처리하는 메서드 -------------------------------------------------------------------
  @ResponseBody
  @GetMapping("/comment/{bno}") // HTTP GET 요청을 처리하며, URL 경로의 {bno}를 매핑하여 게시글 번호로 사용
  public List<BoardCommentDTO> getMethodName(@PathVariable int bno, @RequestParam int start) {

    // 1. 게시글 번호(bno)와 시작 인덱스(start)를 이용하여 댓글 리스트를 조회
    // - @PathVariable: URL 경로에서 {bno} 값을 추출하여 매개변수 bno에 매핑
    // - @RequestParam: 쿼리 파라미터(start)의 값을 추출하여 매개변수 start에 매핑
    // - 예: /comment/123?start=6 -> bno = 123, start = 6

    // 2. 서비스 계층을 호출하여 댓글 리스트를 가져옴
    // - boardService.getCommentList(bno, start):
    // - bno: 특정 게시글의 댓글만 조회하기 위해 게시글 번호 전달
    // - start: 조회 시작 인덱스를 전달 (MyBatis 쿼리에서 RW 조건으로 사용)
    // - 댓글 5개씩 조회하기 위해 start와 start+4 범위를 쿼리에서 설정

    List<BoardCommentDTO> commentList = boardService.getCommentList(bno, start);

    // 3. 조회된 댓글 리스트를 클라이언트에 JSON 형식으로 반환
    // - @ResponseBody: 반환값을 JSON 형식으로 직렬화하여 클라이언트에 전송
    // - List<BoardCommentDTO>: 댓글 정보를 담은 DTO 객체 리스트를 JSON 형태로 응답
    // - 클라이언트는 JSON 응답을 파싱하여 화면에 댓글을 추가로 표시

    return commentList;
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
     *    - 로그인된 사용자는 댓글 싫어요를 추가하거나, 이미 싫어요를 누른 경우 싫어요를 취소합니다.
     *    - 비로그인 사용자는 경고 메시지를 반환합니다.
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
     * - value: 결과 데이터를 담은 값(Object). 예: 상태 코드(Integer), 메시지(String), 싫어요 개수(Integer)
     * 
     * @ResponseBody 어노테이션 덕분에 이 Map 객체는 자동으로 JSON 형식으로 변환되어 클라이언트로 전송됩니다.
     * 예시 응답:
     * {"code":1, "msg":"해당 댓글에 싫어요 하셨습니다.", "count":3}
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
     *   예: BoardCommentDTO { id="작성자ID", content="댓글 내용", bno=123 }
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
     *   - code: 작업 성공 여부를 나타내는 상태 코드 (1: 성공, 2: 실패)
     *   - msg: 처리 결과 메시지
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

}