package com.member.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.member.dto.BoardMemberDTO;
import com.member.dto.GradeDTO;
import com.member.mapper.MemberMapper;

@Service
public class MemberService {
	private MemberMapper mapper;

	public MemberService(MemberMapper mapper) {
		this.mapper = mapper;
	}

	public BoardMemberDTO login(String id, String passwd) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("pass", passwd);
		
		return mapper.login(map);
	}

	public List<BoardMemberDTO> selectAllMember() {
		return mapper.selectAllMember();
	}

	public BoardMemberDTO selectMember(String id) {
		return mapper.selectMember(id);
	}

	public int updateMember(BoardMemberDTO dto) {
		return mapper.updateMember(dto);
	}

	public int deleteMember(String id) {
		return mapper.deleteMember(id);
	}

	public int insertMember(BoardMemberDTO dto) throws SQLException{
		return mapper.insertMember(dto);
	}

	public List<GradeDTO> selectAllGrade() {
		return mapper.selectAllGrade();
	}

	public int updateGrade(GradeDTO dto) {
		return mapper.updateGrade(dto);
	}

	public int insertGrade(GradeDTO dto) {
		return mapper.insertGrade(dto);		
	}

	public int deleteGrade(int gradeNo) {
		return mapper.deleteGrade(gradeNo);
	}

	public List<BoardMemberDTO> selectGradeMember(int[] gradeNo) {
		return mapper.selectGradeMember(gradeNo);
	}
	
}











