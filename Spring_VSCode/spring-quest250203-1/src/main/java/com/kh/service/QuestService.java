package com.kh.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.quest.dto.QuestDTO;
import com.quest.mapper.QuestMapper;

@Service
public class QuestService {
	private QuestMapper mapper;

	public QuestService(QuestMapper mapper) {
		this.mapper = mapper;
	}

	public List<QuestDTO> selectAllHistory() {
		return mapper.selectAllHistory();
	}

	public int insertHistory(String search) {
		return mapper.insertHistory(search);
	}
	
	
}
