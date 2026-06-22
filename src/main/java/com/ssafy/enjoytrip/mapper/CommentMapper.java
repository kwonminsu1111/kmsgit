package com.ssafy.enjoytrip.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.enjoytrip.dto.response.CommentResponse;

@Mapper
public interface CommentMapper {
	// 1. 댓글 등록
    int insertComment(@Param("boardId") Long boardId, 
                      @Param("userId") String userId, 
                      @Param("content") String content);

    // 2. 특정 게시글에 달린 댓글 목록 전체 조회
    List<CommentResponse> selectCommentsByBoardId(Long boardId);

    // 3. 댓글 단건 조회
    CommentResponse selectCommentById(Long id);

    // 4. 댓글 삭제
    int deleteComment(Long id);
}
