package com.kh.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.quest.dto.QuestDTO;

@Mapper
public interface QuestMapper {
	int insertHistory(String search);
	List<QuestDTO> selectAllHistory();
}
