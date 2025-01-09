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

	public List<BoardMemberDTO> selectNameMember(String name) {
		return mapper.selectNameMember(name);
	}

	public int deleteMember(String id) {
		return mapper.deleteMember(id);
	}

	public List<BoardMemberDTO> selectAllMember() {
		return mapper.selectAllMember();
	}

	public int selectIdMember(String id) {
		return mapper.selectIdMember(id);
	}

	public BoardMemberDTO login(String id, String password) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", id);
			map.put("password", password);
			return mapper.findMemberByIdAndPassword(map);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

  public int updateMember(Map<String, String> map) {
		return mapper.updateMember(map);
	}

  public int updateColumnMember(Map<String, String> body) {
		return mapper.updateColumnMember(body);
	}

  public int insertMember(BoardMemberDTO dto) {
		return mapper.insertMember(dto);
	}
	
}






