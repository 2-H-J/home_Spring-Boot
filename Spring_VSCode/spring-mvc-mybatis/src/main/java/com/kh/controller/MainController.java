package com.kh.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dto.BoardMemberDTO;
import com.kh.service.BoardMemberService;

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
	
	@GetMapping("/members")
	public ModelAndView allMembers(ModelAndView view) {
		//전체 회원 정보 받아옴
		List<BoardMemberDTO> list = service.selectAllMember();
		
		view.addObject("list", list);
		view.setViewName("member_list");
		return view;
	}
	
	@GetMapping("/member/register/view")
	public String registerView() {
		return "member_insert_view";
	}
	
	@PostMapping("/member/register")
	public String memberRegister(BoardMemberDTO member) {
		System.out.println(member);
		service.insertMember(member);
		return "redirect:/members"; 
	}
	
	@GetMapping("/member/delete/{id}")
//	public String deleteMember(@PathVariable("id") String id) {
	public String deleteMember(@PathVariable String id) {
		System.out.println("삭제할 아이디 : " + id);
		int count = service.deleteMember(id);
		
		if(count == 0) {
			System.out.println("데이터 삭제 실패");
		}else {
			System.out.println("데이터 삭제 성공");
		}
		
		return "redirect:/members";
	}
	
	@GetMapping("/member/{id}")
	public ModelAndView updateView(ModelAndView view, @PathVariable String id) {
		
		BoardMemberDTO member = service.selectMember(id);
		
		view.addObject("member", member);
		view.setViewName("member_update_view");
		
		return view;
	}
	
	@PostMapping("/member/update")
	public String memberUpdate(BoardMemberDTO member) {
		System.out.println(member);
		service.updateMember(member);
		return "redirect:/members"; 
	}
	
}






