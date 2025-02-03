package com.kh.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(allowedHeaders = "*", origins = "*")
@RestController
public class KakaoController {
	private final String REST_API_KEY = "REST_API_KEY";
	private final String REDIRECT_URI = "http://localhost:3000/kakao/login/oauth";
	
	@GetMapping("/kakao/token")
	public String kakaoCallBack(String code) {
		
		String apiURL = "https://kauth.kakao.com/oauth/token?";
		apiURL += "grant_type=authorization_code"
				+ "&client_id=" + REST_API_KEY
				+ "&redirect_uri=" + REDIRECT_URI
				+ "&code=" + code;
		
		String res = requestKakaoServer(apiURL, null);
		
		if(res != null && !res.equals("")) {
			return res;
		}
		
		return "로그인실패";
	}
	
	@GetMapping("/kakao/profile")
	public String getProfile(String token) {
		System.out.println(token);
		String header = "Bearer " + token;
		String apiURL = "https://kapi.kakao.com/v2/user/me";
		
		String res = requestKakaoServer(apiURL, header);
		System.out.println(res);
		return res;
	}
	
	@GetMapping("/kakao/delete")
	public String  deleteToken(String token) {
		String header = "Bearer " + token;
		String apiURL = "https://kapi.kakao.com/v1/user/unlink";
		
		String res = requestKakaoServer(apiURL, header);
		System.out.println(res);
		return res;
	}
	
	public String requestKakaoServer(String apiURL, String header) {
		StringBuffer res = new StringBuffer();
		try {
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			if (header != null && !header.equals("")) {
				con.setRequestProperty("Authorization", header);
			}

			int responseCode = con.getResponseCode();
			BufferedReader br;
			System.out.print("responseCode=" + responseCode);
			if (responseCode == 200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else { // 에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				res.append(inputLine);
			}
			br.close();
			if (responseCode == 200) {
				System.out.println(res.toString());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return res.toString();
	}
	
}









