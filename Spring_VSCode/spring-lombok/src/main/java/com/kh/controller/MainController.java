package com.kh.controller;

import java.time.Period;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.dto.Person;

@Controller
public class MainController {
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/call")
	@ResponseBody
	public String call(String name, int age) {
		Person p = new Person(name,age);
		return p.toString();
	}
}
