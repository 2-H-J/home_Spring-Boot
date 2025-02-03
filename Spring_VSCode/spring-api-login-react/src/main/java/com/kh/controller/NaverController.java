package com.kh.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(allowedHeaders = "*", origins = "*")
@RestController
public class NaverController {
	private final String CLIENT_ID = "CLIENT_ID";
	private final String CLIENT_SECRET_ID = "CLIENT_SECRET_ID";
	
	@GetMapping("/naver/token")
	public Map<String, Object> naverCallBack(String state, String code)
			throws UnsupportedEncodingException, JsonMappingException, JsonProcessingException {
		String redirectURI = URLEncoder.encode("http://localhost:3000/naver/login/oauth", "UTF-8");
		String apiURL;
		apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&";
		apiURL += "client_id=" + CLIENT_ID;
		apiURL += "&client_secret=" + CLIENT_SECRET_ID;
		apiURL += "&redirect_uri=" + redirectURI;
		apiURL += "&code=" + code;
		apiURL += "&state=" + state;
		
		String res = requestNaverServer(apiURL, null);
		Map<String, Object> map = new HashMap<>();
		if(res != null && !res.equals("")) {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, String> paramsMap = objectMapper.readValue(res, new TypeReference<Map<String, String>>() {
			});
			map.put("user", res);
			map.put("accessToken", paramsMap.get("access_token"));
			map.put("refreshToken", paramsMap.get("refresh_token"));
		}else {
			map.put("msg", "토큰 획득에 실패하였습니다.");
		}
		
		return map;
	}

	@GetMapping("/naver/profile")
	public String getProfile(String token) {
		String header = "Bearer " + token; 
        String apiURL = "https://openapi.naver.com/v1/nid/me";
        
        String result = requestNaverServer(apiURL, header);
        
		return result;
	}
	
	@GetMapping("/naver/refresh")
	public Map<String, Object> refreshToken(String token) throws JsonMappingException, JsonProcessingException {
		String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=refresh_token"
				+ "&client_id=" + CLIENT_ID
				+ "&client_secret=" + CLIENT_SECRET_ID
				+ "&refresh_token=" + token;

		String res = requestNaverServer(apiURL, null);
		
		Map<String, Object> map = new HashMap<>();
		if(res != null && !res.equals("")) {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, String> paramsMap = objectMapper.readValue(res, new TypeReference<Map<String, String>>() {
			});
			map.put("user", res);
			map.put("accessToken", paramsMap.get("access_token"));
			map.put("refreshToken", paramsMap.get("refresh_token"));
		}else {
			map.put("msg", "토큰 획득에 실패하였습니다.");
		}
		
		return map;
	}
	
	@GetMapping("/naver/delete")
	public String deleteToken(String token) {
		String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=delete"
				+ "&client_id=" + CLIENT_ID
				+ "&client_secret=" + CLIENT_SECRET_ID
				+ "&access_token=" + token;

		String res = requestNaverServer(apiURL, null);
		System.out.println("delete : " + res);
		return res;
	}
	
	public String requestNaverServer(String apiURL, String header) {
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






