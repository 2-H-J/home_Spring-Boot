package com.kh.service;

import org.springframework.stereotype.Service;

import com.kh.mapper.BoardMemberMapper;

@Service // Service class에서 쓰이는 어노테이션으로, 비즈니스 로직을 수행하는 Class라는 것을 나타내는 용도
public class BoardMemberService {
	
	private BoardMemberMapper mapper;

	// mapper 역주입해서 가져오기
	public BoardMemberService(BoardMemberMapper mapper) {
		this.mapper = mapper;
	}
	
}
