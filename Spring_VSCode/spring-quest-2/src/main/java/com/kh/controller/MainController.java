package com.kh.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dto.BookDTO;
import com.kh.service.QuestService;

@Controller
public class MainController {
	private QuestService service;

	public MainController(QuestService service) {
		super();
		this.service = service;
	}

	@GetMapping("/")
	public ModelAndView main(ModelAndView view) {
		List<BookDTO> list = service.selectAll();
		view.addObject("list", list);
		view.setViewName("index");
		return view;
	}

	@ResponseBody
	@PostMapping("/search")
	public List<BookDTO> search(@RequestBody Map<String, String> map) {
		System.out.println(map.get("title"));
		List<BookDTO> list = service.search(map.get("title"));
		return list;
	}
	
	@GetMapping("/delete")
	public String deleteCar(String isbn) {
		service.delete(isbn);
		return "redirect:/";
	}
}
