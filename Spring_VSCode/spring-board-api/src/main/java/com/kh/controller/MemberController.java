package com.kh.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.kh.service.BoardMemberService;
import com.kh.token.JwtTokenProvider;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
// @CrossOrigin : CORS(Cross-Origin Resource Sharing)을 통해 다른 도메인의 요청을 허용하는 컨트롤러 - 다른 출처의 자원을 공유
// origins : 새로운 자원를 공유하는 도메인(허용하는 도메인) - 여러개인 경우 origins = {"http://localhost:8000", "http://localhost:8001"} 이렇게 표현, * = 전체
// allowedHeaders : 실제 요청 중에 사용할 수 있는 요청 헤더 목록이다. pre-flight의 응답 헤더인 access-control-allow-header에 값이 사용
public class MemberController {
    // 로그인 하기 위한 Service
    private final BoardMemberService memberService;
    // TokenProvider
    private final JwtTokenProvider tokenProvider;

    // 생성자
    public MemberController(BoardMemberService memberService, JwtTokenProvider tokenProvider) {
        this.memberService = memberService;
        this.tokenProvider = tokenProvider;
    }

    
}
