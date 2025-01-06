package com.kh.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dto.BoardMemberDTO;
import com.kh.service.BoardMemberService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class MainController {
	private BoardMemberService service;

	public MainController(BoardMemberService service) {
		this.service = service;
	}
	

	@GetMapping("/")
	public ModelAndView index(ModelAndView view) {
		view.setViewName("index");
		return view;
	}
	
	@GetMapping("/logout")
	public ModelAndView logoutView(ModelAndView view) {
		view.setViewName("index");
		return view;
	}

	@PostMapping("/login")
	public String login(String id, String passwd, HttpSession session) {
		BoardMemberDTO member = service.login(id, passwd);

		System.out.println(member);

		//로그인 실패 했을 때 
		if(member == null) {
			return "redirect:/";
		}

		//로그인 성공
		session.setAttribute("member", member);

		return "redirect:/members";
	}
	
	
	
	@GetMapping("/members")
	public ModelAndView members(ModelAndView view) {
		List<BoardMemberDTO> list = service.selectAllMember();
		view.addObject("list", list);
		view.setViewName("main");
		return view;
	}

	@GetMapping("/member/register/view")
	public String registerView() {
		return "member_insert_view";
	}

	@PostMapping("/member/register")
	public String memberRegister(@RequestBody BoardMemberDTO member) {
		System.out.println(member);
		service.insertMember(member);
		return "redirect:/members"; 
	}
	
	//회원정보 삭제 메서드
	@GetMapping("/member/delete/{id}")
	public String deleteMember(@PathVariable String id) {
		System.out.println("[Controller] 회원update 확인 : "+id);
		
		int count = service.deleteMember(id);
		if(count == 0) {
			System.out.println("데이터 삭제 실패");
		}else {
			System.out.println("데이터 삭제 성공");
		}
		
		return "redirect:/members";
	}
	
	//회원정보 수정 페이지 이동 메서드
	@GetMapping("/member/{id}")
	public ModelAndView updateMemberView(ModelAndView view, @PathVariable String id) {
		System.out.println("[Controller] 회원update view 확인 : "+id);
		
		BoardMemberDTO member = service.selectMember(id);
		
		view.addObject("member",member);
		view.setViewName("member_update_view");
		return view;
	}
	
	
	
	
}






