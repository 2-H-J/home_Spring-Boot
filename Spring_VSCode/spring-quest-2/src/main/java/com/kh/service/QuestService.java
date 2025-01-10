package com.kh.service;

import java.util.List;

import org.springframework.stereotype.Service;

// import org.springframework.stereotype.Service;

import com.kh.dto.BookDTO;
import com.kh.mapper.QuestMapper;

@Service // 애노테이션 추가
public class QuestService {
	private QuestMapper mapper;

	public QuestService(QuestMapper mapper) {
		this.mapper = mapper;
	}

  public List<BookDTO> selectAll() {
		return mapper.selectAll();
  }

  public List<BookDTO> search(String title) {
		return mapper.selectTitle(title);
  }

  public void delete(String isbn) {
		mapper.delete(isbn);
  }

}
