package com.kh.token;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.kh.dto.BoardMemberDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenProvider {
    // Token의 유효시간 결정 설정
    // 형변환이 일어나면 사용자가 많으면 리터럴문자 L적용해서 
	private final long expiredTime = 1000L * 60L * 60L * 1L; // 1시간의 유효시간 설정
    // 1000L * 60L : 1분
    // 1000L * 60L * 60L : 1시간

    // Token 암호와 하기 위해 key값 설정
    // 1. 서버가 재시작 시 매번 key값이 바뀜 : Token의 결과값이 달라짐
	private SecretKey key = Jwts.SIG.HS256.key().build();
    // Key key = Keys.hmacShaKeyFor("256비트 이상인 문자열".getBytes(StandardCharsets.UTF_8));
    // "256비트 이상인 문자열"에 원하는 고정된 key값 설정 가능 암호 보안 단점이 있음
	
	public String generateJwtToken(BoardMemberDTO member) {
		Date expire = new Date(Calendar.getInstance().getTimeInMillis() + expiredTime);
		
        // Token 생성 빌드
		return Jwts.builder().header().add(createHeader()).and()
				.setExpiration(expire).setClaims(createClaims(member))
				.subject(member.getId()).signWith(key).compact();
        // header : Token정보, 암호화할 알고리즘, 생성 시간
        // setExpiration(expire) : 만료일 결정
        // setClaims(createClaims(member)) : 토큰에 필요한 정보를 넣어주는 부분 (관리자 인지, 일반유저인지 권한 적용)
        // subject(member.getId()) : 사용자 Id값 정보 - 고유식별자
        // signWith(key) : 암호화 알고리즘
	}
	
	//토큰에 저장된 로그인 id값 꺼내서 반환
	public String getUserIDFromToken(String token) {
		return (String) getClaims(token).getSubject();
	}
	//토큰에 저장된 권한에 관련된 값 꺼내서 반환
	public String getRoleFromToken(String token) {
		return (String) getClaims(token).get("roles");
	}
	
    // Token에 설정한 정보 가져오기    
	private Claims getClaims(String token) {
		return Jwts.parser().verifyWith(key).build().parseClaimsJws(token).getBody();
	}
	
    // Token 정보 빌드 createClaims메서드    
	private Map<String, Object> createClaims(BoardMemberDTO member) {
		Map<String, Object> map = new HashMap<>();
		map.put("roles", member.getGrade() == 5 ? "Admin" : "User");
		return map;
	}

    // Token 생성 빌드 createHeader메서드    
	private Map<String, Object> createHeader() {
		Map<String, Object> map = new HashMap<>();
		map.put("typ", "JWT");//토큰 종류
		map.put("alg", "HS256");//암호화에 사용할 알고리즘
		map.put("regDate", System.currentTimeMillis());//생성시간
		return map;
	}
}