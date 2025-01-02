package com.kh.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dto.BoardMemberDTO;
import com.kh.service.BoardMemberService;
// 1
@Controller //Spring MVC의 Controller로 사용되는, 클래스 선언을 단순화 시켜주는 어노테이션
public class MainController {
	private BoardMemberService service;

	public MainController(BoardMemberService service) {
		this.service = service;
	}
	
	
	
	@GetMapping("/") // @RequestMapping(Method=RequestMethod.GET)과 같은 역할을 한다.
	public ModelAndView index(ModelAndView view) {
		view.setViewName("index");
		return view;
	}
	

	
//	멤버 리스트 페이지--------------------------------------------------------------------------
	@GetMapping("/members")
	public ModelAndView allMembers(ModelAndView view) {
		// 전체 회원정보 받아옴
		List<BoardMemberDTO> list = service.selectAllMember();
		
		view.addObject("list",list);
		
		view.setViewName("member_list");
		return view;
	}
	
//	멤버 추가 페이지--------------------------------------------------------------------------
	@GetMapping("/member/register/view")
	public String registerView() {
		return "member_insert_view";
	}
	
//	멤버 추가--------------------------------------------------------------------------
	@PostMapping("/member/register")
	public String memberRegister(BoardMemberDTO member) {
		System.out.println("[Controller] 회원insert 확인 : "+member);
		
		service.insertMember(member);
		return "redirect:/members";
	}
	
//	멤버 삭제--------------------------------------------------------------------------
//	public String deleteMember(@PathVariable("id") String id) {
	// @PathVariable("id")는 값이 다를경우 입력하여도 된다 이미 /{id}를 했기 때문에
	@GetMapping("/member/delete/{id}")
	public String deleteMember(@PathVariable String id) { 
		System.out.println("[Controller] 회원delete 확인 : "+id);
		
		int count = service.deleteMember(id);
		if(count == 0) {
			System.out.println("데이터 삭제 실패");
		}else {
			System.out.println("데이터 삭제 성공");
		}
		return "redirect:/members";
	}
	
//	멤버 수정 페이지--------------------------------------------------------------------------
	@GetMapping("/member/{id}")
	public ModelAndView updateView(ModelAndView view, @PathVariable String id) {
		System.out.println("[Controller] 회원update 확인 : "+id);
		
		BoardMemberDTO member = service.selectMember(id);
		
		view.addObject("member",member);
		view.setViewName("member_update_view");
		return view;
	}
//	멤버 수정 하기--------------------------------------------------------------------------
	@PostMapping("/member/update")
	public String memberUpdate(BoardMemberDTO member) {
		System.out.println("[Controller] 회원insert 확인 : "+member);
		
		service.updateMember(member);
		return "redirect:/members";
	}
	
	
}
