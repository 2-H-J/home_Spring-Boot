package com.kh.token;

// 필요한 클래스와 라이브러리 import
import java.util.Calendar; // 현재 날짜와 시간을 가져오기 위한 클래스
import java.util.Date; // 날짜 관련 데이터를 처리하기 위한 클래스
import java.util.HashMap; // Key-Value 구조 데이터를 저장하기 위한 클래스
import java.util.Map; // Key-Value 구조 데이터를 위한 인터페이스

import javax.crypto.SecretKey; // 암호화를 위한 비밀 키를 생성하고 관리하는 인터페이스

import org.springframework.stereotype.Component; // 스프링 프레임워크에서 해당 클래스를 Bean으로 등록하기 위한 어노테이션

import com.kh.dto.BoardMemberDTO; // 사용자의 정보를 담은 DTO 클래스

import io.jsonwebtoken.Claims; // JWT의 Claims(토큰 본문 데이터)를 다루기 위한 클래스
import io.jsonwebtoken.Jwts; // JWT 토큰을 생성하고 파싱하기 위한 클래스

/**
 * JwtTokenProvider 클래스는 JWT 토큰을 생성, 검증, 그리고 정보를 추출하는 역할을 한다.
 * @Component 어노테이션을 사용해 이 클래스를 Spring의 Bean으로 등록하여 다른 클래스에서 의존성 주입을 받을 수 있도록 한다.
 */
@Component // Spring이 관리하는 Bean으로 등록. 필요한 곳에서 이 클래스를 의존성 주입으로 사용 가능.
public class JwtTokenProvider {
    // Token의 유효시간 결정 설정
    private final long expiredTime = 1000L * 60L * 60L * 1L; // 토큰 유효 시간을 1시간으로 설정
    // 1000L : 1초를 밀리초로 표현 (1000ms = 1초)
    // 60L : 1분 (60초)
    // 60L * 60L : 1시간 (3600초)
    // 1L은 long 타입 리터럴 표시를 위한 것.

    // Token 암호화를 위한 SecretKey 객체 생성
    private SecretKey key = Jwts.SIG.HS256.key().build(); // HS256 알고리즘으로 비밀 키를 생성
    // HS256은 대칭 키 암호화 방식으로, 암호화와 복호화에 같은 키를 사용한다.
    // Jwts.SIG.HS256.key()는 io.jsonwebtoken의 메서드로, HS256 알고리즘을 사용하는 키 객체를 생성한다.

    /**
     * JWT 토큰 생성 메서드
     * @param member 사용자 정보를 담은 DTO 객체
     * @return 생성된 JWT 토큰
     */
    public String generateJwtToken(BoardMemberDTO member) {
        Date expire = new Date(Calendar.getInstance().getTimeInMillis() + expiredTime);
        // 토큰 만료 시간을 현재 시간에 유효 시간을 더한 값으로 설정.
        // Calendar.getInstance().getTimeInMillis(): 현재 시간을 밀리초로 반환.
        // new Date() 생성자를 통해 java.util.Date 객체 생성.

        // JWT 토큰 생성 및 반환
        return Jwts.builder()
                .header().add(createHeader()).and() // header 정보 추가
                .setExpiration(expire) // 만료 시간 설정
                .setClaims(createClaims(member)) // Claims(본문 데이터) 설정
                .subject(member.getId()) // 사용자 고유 ID 설정
                .signWith(key) // 생성된 키로 서명
                .compact(); // 최종적으로 토큰을 생성하여 문자열로 반환
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String getUserIDFromToken(String token) {
        return getClaims(token).getSubject(); // Claims에서 subject(사용자 ID)를 가져옴
    }

    /**
     * JWT 토큰에서 사용자 권한(Role) 정보 추출
     * @param token JWT 토큰
     * @return 사용자 권한 (roles)
     */
    public String getRoleFromToken(String token) {
        return (String) getClaims(token).get("roles"); // Claims에서 roles 키의 값을 가져옴
    }

    /**
     * JWT 토큰에서 Claims(본문 데이터) 가져오기
     * @param token JWT 토큰
     * @return Claims 객체
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key) // 생성된 SecretKey를 사용해 서명을 검증
                .build() // 빌더 객체 생성
                .parseClaimsJws(token) // 토큰 파싱 및 검증
                .getBody(); // Claims(본문 데이터) 반환
    }

    /**
     * 사용자 정보를 기반으로 Claims 생성
     * @param member 사용자 정보를 담은 DTO 객체
     * @return Claims에 포함할 데이터
     */
    private Map<String, Object> createClaims(BoardMemberDTO member) {
        Map<String, Object> map = new HashMap<>();
        // 사용자 권한 정보 추가
        map.put("roles", member.getGrade() == 5 ? "Admin" : "User");
        // 사용자 등급(Grade)이 5이면 Admin 권한, 그렇지 않으면 일반 User 권한 설정
        return map;
    }

    /**
     * JWT Header 생성
     * @return Header에 포함할 데이터
     */
    private Map<String, Object> createHeader() {
        Map<String, Object> map = new HashMap<>();
        map.put("typ", "JWT"); // 토큰 유형(Type) 설정
        map.put("alg", "HS256"); // 암호화 알고리즘 설정
        map.put("regDate", System.currentTimeMillis()); // 토큰 생성 시간
        return map;
    }
}

/**
 * 추가 설명:
 * 
 * 1. 클래스 설명:
 *    이 클래스는 JWT 토큰을 생성, 검증, 파싱하는 데 필요한 기능을 제공.
 *    Spring의 @Component 어노테이션을 통해 Bean으로 등록되며, 다른 클래스에서 의존성 주입으로 사용 가능.
 * 
 * 2. 주요 구성:
 *    - `expiredTime`: 토큰의 유효 시간을 설정하는 상수.
 *    - `key`: HS256 알고리즘을 사용하는 암호화 키.
 *    - `generateJwtToken`: 사용자 정보를 기반으로 JWT 토큰을 생성.
 *    - `getUserIDFromToken`: 토큰에서 사용자 ID 추출.
 *    - `getRoleFromToken`: 토큰에서 권한 정보(Role) 추출.
 *    - `getClaims`: 토큰의 Claims(본문 데이터)를 파싱.
 *    - `createClaims`: 사용자 정보를 기반으로 Claims 데이터 생성.
 *    - `createHeader`: JWT Header 데이터 생성.
 * 
 * 3. 코드 흐름:
 *    1) 사용자가 로그인하거나 인증에 성공하면 `generateJwtToken` 메서드가 호출되어 JWT 토큰 생성.
 *    2) 토큰 생성 시 Header, Claims, Subject, Expiration, SignWith가 설정됨.
 *    3) 이후 생성된 토큰은 클라이언트로 전달되어 인증에 사용됨.
 *    4) 클라이언트가 보낸 토큰은 서버에서 검증하며, 필요한 정보를 추출해 처리.
 */
