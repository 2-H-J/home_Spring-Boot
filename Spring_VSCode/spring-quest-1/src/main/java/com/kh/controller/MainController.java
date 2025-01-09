package com.kh.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dto.CarDTO;
import com.kh.service.QuestService;


@Controller
public class MainController {
	
	private QuestService service;

	// 생성자
	public MainController(QuestService service) {
		this.service = service;
	}

	@GetMapping("/")
	public ModelAndView main(ModelAndView view) {
		List<CarDTO> list = service.selectAllCar();
		view.addObject("list", list);
		view.setViewName("index");
		return view;
	}
	
	@ResponseBody // (AJAX POST 요청)
	@PostMapping("/search")
	public ResponseEntity<Object> search(@RequestBody Map<String, String> map) {
		List<CarDTO> list = service.searchCar(map.get("carName"));
		return ResponseEntity.ok().body(list);
	}

	
	@GetMapping("/delete")
	public String delete(@RequestParam String carId) {
		service.deleteCar(carId);
		return "redirect:/";
	}
	
}
