package com.kh.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dto.BookDTO;

@Mapper
public interface QuestMapper {

  List<BookDTO> selectAll();

  void delete(String isbn);

  List<BookDTO> selectTitle(String title);


}
