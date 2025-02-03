package com.kh.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dto.CarDTO;
import com.kh.service.QuestService;

@Controller
public class MainController {
	private QuestService service;

	public MainController(QuestService service) {
		this.service = service;
	}
	
	@GetMapping("/")
	public ModelAndView main(ModelAndView view) {
		List<CarDTO> list = service.selectAllCar();
		
		view.addObject("list",list); //
		view.setViewName("quest_main");

		return view;
	}
	
	
	@PostMapping("/insert")
	public String insert(CarDTO dto) {
		service.insertCar(dto);
		return "redirect:/";
	}
}
