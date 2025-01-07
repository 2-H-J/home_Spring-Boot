package com.kh.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dto.BoardDTO;
import com.kh.dto.BoardMemberDTO;
import com.kh.service.BoardMemberService;
import com.kh.service.BoardService;
import com.kh.vo.PaggingVO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {

    private BoardService boardService;
    private BoardMemberService boardMemberService;

    public HomeController(BoardService boardService, BoardMemberService boardMemberService) {
        this.boardService = boardService;
        this.boardMemberService = boardMemberService;
    }

    @GetMapping({"/", "/main"})
    public ModelAndView index(@RequestParam(defaultValue = "1") int pageNo, @RequestParam(defaultValue = "30") int pageContentEa, ModelAndView view) { // 30개의 게시글 조회

        // 전체 게시글 갯수 조회
        int count = boardService.selectBoardTotalCount();

        // 페이지 번호를 보내서 해당 페이지 게시글 목록만 조회
        List<BoardDTO> boardList = boardService.getBoardList(pageNo, pageContentEa);

        // PaggingVO 페이징 정보 생성
        PaggingVO paggingVO = new PaggingVO(count, pageNo, pageContentEa);
        // count : 전체 게시글 개수, pageNo : 이전 페이지 그룹번호, pageContentEa : 한페이지당 출력할 게시글 개수

        // 데이터 추가
        view.addObject("boardList", boardList); // 게시글 목록
        view.addObject("paggingVO", paggingVO); // 페이징 정보

        // 이동할 페이지 설정
        view.setViewName("index");

        return view;
    }

    @GetMapping("/login/view")
    public String loginView() {
        return "login";
    }


    @PostMapping("/login")
    public String login(String id, String password, HttpSession session, HttpServletResponse response) throws IOException {
        /* 
        - 로그인 수행시 HomeController에서 로그인 수행
        
        완료 시 - index.html로 이동
        로그인 정보는 세션에 저장(user로 저장) 
         */
        BoardMemberDTO member = boardMemberService.login(id, password);
        if(member != null) {
          session.setAttribute("user", member);
          return "redirect:/main";
        }

        response.setContentType("text/html; charset=utf-8");
        response.getWriter().println(
          "<script>alert('로그인 실패\\n아이디와 비밀번호 확인하세요');" + 
          "history.back();</script>"); 
  
        return null;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) { 
        session.invalidate(); // invalidate : 무효화
        return "redirect:/main";
    }

    

}