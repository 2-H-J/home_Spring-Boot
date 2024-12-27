package com.kh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kh.service.BoardMemberService;

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
	
	
}
