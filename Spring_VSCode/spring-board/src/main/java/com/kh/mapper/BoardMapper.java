package com.kh.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dto.BoardDTO;
import com.kh.dto.BoardCommentDTO;
import com.kh.dto.BoardFileDTO;

@Mapper
public interface BoardMapper {

    // 페이지별 게시글 목록 조회
    // 입력: 페이지 및 검색 조건이 담긴 Map 객체
    List<BoardDTO> getBoardList(Map<String, Object> map);
    // 게시글 등록
    // 입력: BoardDTO 객체(제목, 내용 등 게시글 정보)
    int insertBoard(BoardDTO dto);
    // 특정 게시글 상세 조회
    // 입력: 게시글 번호 (bno)
    BoardDTO selectBoard(int bno);
    // 게시글 조회수 증가
    // 입력: 게시글 번호 (bno)
    int updateBoardCount(int bno);
    // 댓글 등록
    // 입력: 댓글 정보가 담긴 BoardCommentDTO 객체
    int insertBoardComment(BoardCommentDTO dto);
    // 특정 게시글의 댓글 목록 조회
    // 입력: 게시글 번호 (bno)
    List<BoardCommentDTO> getCommentList(int bno);
    // 게시글 삭제
    // 입력: 삭제할 게시글 번호 (bno)
    int deleteBoard(int bno);
    // 댓글 삭제
    // 입력: 삭제할 댓글 번호 (cno)
    int deleteBoardComment(int cno);
    // 게시글 수정
    // 입력: 수정할 게시글 정보가 담긴 BoardDTO 객체
    int updateBoard(BoardDTO dto);
    // 새 게시글 번호 가져오기
    int selectBoardNo();
    // 게시글에 파일 등록
    // 입력: 파일 정보가 담긴 BoardFileDTO 객체
    int insertBoardFile(BoardFileDTO item);
    // 특정 게시글에 첨부된 파일 목록 조회
    // 입력: 게시글 번호 (bno)
    List<BoardFileDTO> getBoardFileList(int bno);
    // 특정 파일 경로 조회
    // 입력: 파일 번호 (fno)
    // 출력: 파일 경로 (String)
    String selectFilePath(int fno);
    // 게시글 좋아요 추가
    // 입력: 사용자 및 게시글 정보가 담긴 Map 객체
    int insertBoardLike(Map<String, Object> map);
    // 게시글 좋아요 삭제
    // 입력: 사용자 및 게시글 정보가 담긴 Map 객체
    int deleteBoardLike(Map<String, Object> map);
    // 게시글 싫어요 추가
    // 입력: 사용자 및 게시글 정보가 담긴 Map 객체
    int insertBoardHate(Map<String, Object> map);
    // 게시글 싫어요 삭제
    // 입력: 사용자 및 게시글 정보가 담긴 Map 객체
    int deleteBoardHate(Map<String, Object> map);
    // 특정 게시글의 싫어요 수 조회
    // 입력: 게시글 번호 (bno)
    int getBoardHate(int bno);
    // 특정 게시글의 좋아요 수 조회
    // 입력: 게시글 번호 (bno)
    int getBoardLike(int bno);
    // 댓글 좋아요 삭제
    // 입력: 사용자 및 댓글 정보가 담긴 Map 객체
    int deleteBoardCommentLike(Map<String, Object> map);
    // 댓글 좋아요 추가
    // 입력: 사용자 및 댓글 정보가 담긴 Map 객체
    int insertBoardCommentLike(Map<String, Object> map);
    // 댓글 싫어요 삭제
    // 입력: 사용자 및 댓글 정보가 담긴 Map 객체
    int deleteBoardCommentHate(Map<String, Object> map);
    // 댓글 싫어요 추가
    // 입력: 사용자 및 댓글 정보가 담긴 Map 객체
    int insertBoardCommentHate(Map<String, Object> map);
    // 게시글의 전체 개수 조회
    // 출력: 전체 게시글 수
    int selectBoardTotalCount();
}
