package com.kh.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dto.BoardCommentDTO;
import com.kh.dto.BoardDTO;
import com.kh.dto.BoardFileDTO;

@Mapper
public interface BoardMapper {
	//페이지별 게시글 목록 조회	
	List<BoardDTO> getBoardList(Map<String, Object> map);
	//게시글 등록
	int insertBoard(BoardDTO dto);
	BoardDTO selectBoard(int bno);
	int updateBoardCount(int bno);
	int insertBoardComment(BoardCommentDTO dto);
	List<BoardCommentDTO> getCommentList(Map<String, Object> map);
	int deleteBoard(int bno);
	int deleteBoardComment(int cno);
	int updateBoard(BoardDTO dto);
	int selectBoardNo();
	int insertBoardFile(BoardFileDTO item);
	List<BoardFileDTO> getBoardFileList(int bno);
	String selectFilePath(int fno);
	int insertBoardLike(Map<String, Object> map);
	int deleteBoardLike(Map<String, Object> map);
	int insertBoardHate(Map<String, Object> map);
	int deleteBoardHate(Map<String, Object> map);
	int getBoardHate(int bno);
	int getBoardLike(int bno);
	int deleteBoardCommentLike(Map<String, Object> map);
	int insertBoardCommentLike(Map<String, Object> map);
	int deleteBoardCommentHate(Map<String, Object> map);
	int insertBoardCommentHate(Map<String, Object> map);
	int selectBoardTotalCount();

}




