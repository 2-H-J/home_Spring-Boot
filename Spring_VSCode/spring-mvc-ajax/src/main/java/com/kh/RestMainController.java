package com.kh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RestMainController {

	@GetMapping("/main2")
	public String main() {
		return "index"; 
	}
	
	@GetMapping("/get")
	public RegisterDTO getMethodName() {
		return new RegisterDTO("A0001","김철수","123456",30);
	}
	
	@GetMapping("/map")
	public HashMap<String, Object> returnMap(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("msg", "Exception이 발생되지 않았습니다.");
		map.put("resultCount", 30);
		ArrayList<RegisterDTO> list = new ArrayList<RegisterDTO>();
		list.add(new RegisterDTO("A0001", "홍길동","123456", 30));
		list.add(new RegisterDTO("A0002", "홍길동","123456", 40));
		list.add(new RegisterDTO("A0003", "홍길동","123456", 50));
		list.add(new RegisterDTO("A0004", "홍길동","123456",  60));
		map.put("list", list);
		return map;		
	}
	
	@PostMapping("/call")
	public ResponseEntity<String> call(String data){
		HashMap<String, Object> map = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		if(data.equals("true")) {
			map.put("msg", "Excaption이 발생하지 않았습니다.");
		} else {
			map.put("msg", "Excaption이 발생 했습니다.");
			map.put("errorCode", 777);
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		
		return new ResponseEntity(map,status);
	}
	
	@PostMapping("/login")
	public ResponseEntity<HashMap<String, Object>> login(@RequestBody Map<String, String> body){
		HashMap<String, Object> map = new HashMap<>();
		System.out.println(body.get("id") + " / " + body.get("pass"));
		map.put("msg", "login 호출");
		return ResponseEntity.ok(map);
	}
}










