package com.kh;

import org.springframework.stereotype.Service;

@Service
public class RegisterService {
	
	public RegisterService() {
		System.out.println("Service Constructor");
	}

	public void serviceTest() {
		System.out.println("서비스 클래스 테스트용 메서드");
	}
	
}
