package com.kh.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.kh.dto.BoardMemberDTO;
import com.kh.service.BoardMemberService;
import com.kh.token.JwtTokenProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// @CrossOrigin : CORS(Cross-Origin Resource Sharing)을 통해 다른 도메인의 요청을 허용하는 컨트롤러 - 다른 출처의 자원을 공유
// origins : 새로운 자원를 공유하는 도메인(허용하는 도메인) - 여러개인 경우 origins = {"http://localhost:8000", "http://localhost:8001"} 이렇게 표현, * = 전체
// allowedHeaders : 실제 요청 중에 사용할 수 있는 요청 헤더 목록이다. pre-flight의 응답 헤더인 access-control-allow-header에 값이 사용
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
	
    @PostMapping("/member/login")
    public Map<String, Object> login(@RequestBody Map<String, String> map) {
        Map<String, Object> result = new HashMap<>();
        
        String id = map.get("id");
        String passwd = map.get("passwd");
    
        // 입력 값 검증
        if (id == null || id.isEmpty() || passwd == null || passwd.isEmpty()) {
            result.put("flag", false);
            result.put("message", "ID와 비밀번호를 입력해야 합니다.");
            return result;
        }
    
        // 로그인 서비스 호출
        BoardMemberDTO member = memberService.login(id, passwd);
        if (member != null) {
            // JWT 토큰 생성
            String token = tokenProvider.generateJwtToken(member);
            result.put("flag", true);
            result.put("token", token);
        } else {
            result.put("flag", false);
            result.put("message", "로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    
        return result;
    }
    
	
	
}