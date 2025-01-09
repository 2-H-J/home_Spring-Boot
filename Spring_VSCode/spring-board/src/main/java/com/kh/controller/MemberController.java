package com.kh.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PostMapping;

import com.kh.dto.BoardMemberDTO;
import com.kh.service.BoardMemberService;

// CORS(Cross-Origin Resource Sharing) 설정을 통해 다른 도메인의 요청을 허용하는 컨트롤러
@CrossOrigin(origins = "*", allowedHeaders = "*")
// @CrossOrigin: CORS 정책을 설정하는 애노테이션
// origins = "*": 모든 도메인에서 접근 가능하도록 허용
// allowedHeaders = "*": 모든 요청 헤더를 허용
@RequestMapping("/member") // 컨트롤러 내 모든 메서드가 "/member"로 시작하는 URL과 매핑됨
@Controller // 이 클래스를 Spring MVC의 컨트롤러로 등록
public class MemberController {

  // 서비스 객체, 비즈니스 로직을 처리하는 클래스
  private BoardMemberService service;

  // 생성자를 통해 서비스 객체를 의존성 주입받음
  public MemberController(BoardMemberService service) {
    // Spring이 제공하는 객체를 service 필드에 주입
    // 이 방식은 의존성 주입의 생성자 기반 방법으로, 불변성을 보장하고 더 안전함
    this.service = service;
  }

  // 회원 관리 페이지 요청 처리
  @GetMapping("/main") // GET 요청으로 "/member/main" URL 호출 시 이 메서드가 실행
  public ModelAndView meberMain(ModelAndView view) {
    // 서비스에서 모든 회원 정보를 조회하여 리스트로 반환
    List<BoardMemberDTO> list = service.selectAllMember();

    // 조회한 회원 정보를 ModelAndView 객체에 추가
    view.addObject("list", list);

    // 렌더링할 뷰의 이름 설정 ("admin_member_main.html"로 렌더링)
    view.setViewName("admin_member_main");

    // ModelAndView 객체 반환: 뷰와 데이터를 함께 클라이언트에 전달
    return view;
  }

  // 회원 삭제 요청 처리 (AJAX DELETE 요청)
  @ResponseBody // 메서드 반환값을 JSON으로 변환하여 HTTP 응답 본문에 포함
  @DeleteMapping("/delete") // DELETE 요청으로 "/member/delete" URL 호출 시 실행
  public Map<String, Object> deleteMember(@RequestBody Map<String, Object> param) {
    // 요청 본문(RequestBody)에서 JSON 데이터를 파싱하여 Map 형태로 매핑
    HashMap<String, Object> map = new HashMap<>();

    // 삭제할 회원 ID를 요청 본문에서 추출
    String id = param.get("id").toString();

    // 서비스에서 회원 삭제를 처리 (삭제된 행 수 반환)
    int count = service.deleteMember(id);

    // 삭제 성공 여부에 따라 메시지를 설정
    if (count > 0) {
      map.put("msg", id + " - 해당 아이디 삭제 완료");
    } else {
      map.put("msg", id + " - 삭제할 아이디를 찾을 수 없습니다.");
    }

    // 최신 회원 리스트를 다시 조회하여 응답에 포함
    List<BoardMemberDTO> list = service.selectAllMember();
    map.put("list", list);

    
    return map;
  }

  // 회원 수정 요청 처리 (AJAX PUT 요청)
  @ResponseBody
  @PutMapping("/update") // PUT 요청으로 "/member/update" URL 호출 시 실행
  public Map<String, Object> updateMember(@RequestBody Map<String, String> body) {
    // 요청 본문에서 전달된 데이터를 Map 형태로 파싱
    HashMap<String, Object> map = new HashMap<>();

    // 요청 데이터를 콘솔에 출력 (디버깅용)
    System.out.println(body);

    // 서비스에서 회원 정보를 수정 (수정된 행 수 반환)
    int count = service.updateMember(body);

    // 수정 성공 여부에 따라 메시지를 설정
    if (count > 0) {
      map.put("msg", body.get("id") + " - 해당 아이디 수정 완료");
    } else {
      map.put("msg", body.get("id") + " - 수정할 아이디를 찾을 수 없습니다.");
    }

    // 최신 회원 리스트를 다시 조회하여 응답에 포함
    List<BoardMemberDTO> list = service.selectAllMember();
    map.put("list", list);

    // 결과 메시지와 업데이트된 회원 리스트를 클라이언트에 반환
    return map;
  }

  // 특정 컬럼 수정 요청 처리 (AJAX PATCH 요청)
  @ResponseBody
  @PatchMapping("/updateColumn") // PATCH 요청으로 "/member/updateColumn" URL 호출 시 실행
  public Map<String, Object> updateColumnMember(@RequestBody Map<String, String> body) {
    // 요청 본문에서 전달된 데이터를 Map 형태로 파싱
    HashMap<String, Object> map = new HashMap<>();

    // 요청 데이터를 콘솔에 출력 (디버깅용)
    System.out.println(body);

    // 서비스에서 특정 컬럼 수정 처리 (수정된 행 수 반환)
    int count = service.updateColumnMember(body);

    // 수정 성공 여부에 따라 메시지를 설정
    if (count > 0) {
      map.put("msg", body.get("id") + " - 데이터 수정 완료");
    } else {
      map.put("msg", body.get("id") + " - 수정할 아이디를 찾을 수 없습니다.");
    }

    // 수정 결과만 반환
    return map;
  }

  // 모든 회원 리스트 조회 요청 처리 (React 등에서 사용 가능)
  @ResponseBody
  @GetMapping("/list") // GET 요청으로 "/member/list" URL 호출 시 실행
  public List<BoardMemberDTO> memberList() {
    // 모든 회원 정보를 서비스에서 조회하여 반환
    return service.selectAllMember();
  }

  // 회원 등록 요청 처리 (AJAX POST 요청)
  @ResponseBody
  @PostMapping("/register") // POST 요청으로 "/member/register" URL 호출 시 실행
  public Map<String, Object> register(@RequestBody Map<String, String> body) {
    // 요청 본문 데이터를 Map으로 파싱
    Map<String, Object> map = new HashMap<>();

    // 요청 데이터에서 필요한 값을 추출
    String id = body.get("id");
    String password = body.get("passwd");
    String userName = body.get("name");
    String nickName = body.get("nickname");

    // 회원 정보를 DTO 객체로 생성
    BoardMemberDTO dto = new BoardMemberDTO(id, password, userName, nickName, 0);

    // 서비스에서 회원 등록 처리 (등록된 행 수 반환)
    int count = service.insertMember(dto);

    // 등록 성공 여부에 따라 메시지를 설정
    map.put("count", count);
    if (count > 0) {
      map.put("msg", id + " - 회원가입 완료");
    } else {
      map.put("msg", "회원 가입 실패, 입력하신 데이터를 확인해주세요.");
    }

    // 결과 메시지를 반환
    return map;
  }
}
