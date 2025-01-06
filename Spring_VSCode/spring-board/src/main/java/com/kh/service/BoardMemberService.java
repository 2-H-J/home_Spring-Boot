package com.kh.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.dto.BoardMemberDTO;
import com.kh.mapper.BoardMemberMapper;

@Service
public class BoardMemberService {
	
	private BoardMemberMapper mapper;

	public BoardMemberService(BoardMemberMapper mapper) {
		this.mapper = mapper;
	}

	public List<BoardMemberDTO> selectAllMember() {
		return mapper.selectAllMember();
	}

	public int insertMember(BoardMemberDTO member) {
		return mapper.insertMember(member);
	}

	public int deleteMember(String id) {
		return mapper.deleteMember(id);
	}

	public BoardMemberDTO selectMember(String id) {
		return mapper.selectMember(id);
	}

	public int updateMember(BoardMemberDTO member) {
		return mapper.updateMember(member);
	}

	public BoardMemberDTO login(String id, String passwd) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("passwd", passwd);
		return mapper.login(map);
	}
	
	
}
