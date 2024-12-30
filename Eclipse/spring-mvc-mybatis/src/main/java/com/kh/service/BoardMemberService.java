package com.kh.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.dto.BoardMemberDTO;
import com.kh.mapper.BoardMemberMapper;
// 2
@Service // Service class에서 쓰이는 어노테이션으로, 비즈니스 로직을 수행하는 Class라는 것을 나타내는 용도
public class BoardMemberService {
	
	private BoardMemberMapper mapper;

	// mapper 역주입해서 가져오기
	// 회원 전체 조회--------------------------------------------------------------
	public BoardMemberService(BoardMemberMapper mapper) {
		this.mapper = mapper;
	}

	public List<BoardMemberDTO> selectAllMember() {
		
		return mapper.selectAllMember();
	}

	// 회원 추가--------------------------------------------------------------
	public int insertMember(BoardMemberDTO member) {
		return mapper.insertMember(member); 
	}

	// 회원 삭제--------------------------------------------------------------
	public int deleteMember(String id) {
		return mapper.deleteMember(id);
	}
	
	// 수정 페이지 특정 회원 조회--------------------------------------------------------------
	public BoardMemberDTO selectMember(String id) {
		return mapper.selectMember(id);
	}
	
	// 회원 수정--------------------------------------------------------------
	public int updateMember(BoardMemberDTO member) {
		return mapper.updateMember(member);
	}
	
}
