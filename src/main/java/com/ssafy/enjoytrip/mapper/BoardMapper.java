package com.ssafy.enjoytrip.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.enjoytrip.dto.response.BoardResponse;
import com.ssafy.enjoytrip.model.Board;
import com.ssafy.enjoytrip.model.Hashtag;

@Mapper
public interface BoardMapper {
	// 프론트엔드 반환용 (마이페이지 - 내 활동)
	List<BoardResponse> selectAllBoards(
			@Param("region") String region, 
			@Param("orderBy") String orderBy,
			@Param("filterUserId") Long filterUserId,
			@Param("tags") List<String> tags,
			@Param("myTags") boolean myTags,
			@Param("loginUserId") Long userId
	); // 전체 조회(지역별 필터)
	
	BoardResponse selectBoardResponseById(@Param("id") Long id, 
			@Param("userId") Long userId
	);  // 상세 조회
    
	// 조회수 증가
	int updateHit(Long id);
	
	// 좋아요 증감 로직
	int checkLikeExists(@Param("boardId") Long boardId, @Param("userId") Long userId);
	int insertLike(@Param("boardId") Long boardId, @Param("userId") Long userId);
	int deleteLike(@Param("boardId") Long boardId, @Param("userId") Long userId);
	int updateLikeCount(@Param("boardId") Long boardId, @Param("amount") int amount);
	
    // 벡엔드 내부 로직용 (게시판 - 게시물 작성)
    // 1. 글 쓰기 (Insert)
    int insertBoard(Board board);
    
    int insertBoardHashtags(@Param("boardId") Long boardId, @Param("hashtagId") Integer hashtagId);
    
    // 2. 글 상세 조회 (Select)
    Board selectBoardById(Long id);
    
    // * 게시글 해시태그 조회
    List<String> selectHashtagsByBoardId(Long boardId);
    
    // * 게시글 해시태그 불러오기
    List<Hashtag> selectAllHashtags();
    
    // 3. 게시글 삭제 (Delete)
    int deleteBoard(Long id);
    
    // 4. 게시글 수정 관련 매퍼 메서드 (Update)
    int updateBoard(Board board);
    int deleteBoardHashtags(Long boardId);
}
