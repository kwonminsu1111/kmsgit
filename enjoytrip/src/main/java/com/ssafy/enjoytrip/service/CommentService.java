package com.ssafy.enjoytrip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.enjoytrip.dto.response.CommentResponse;
import com.ssafy.enjoytrip.mapper.CommentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	
	private final CommentMapper commentMapper;
	
	// 1. 댓글 작성
	@Transactional
    public boolean createComment(Long boardId, String userId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 작성하시오.");
        }
        return commentMapper.insertComment(boardId, userId, content) > 0;
    }
	
	// 2. 특정 게시글의 댓글 목록 조회 (Read)
    public List<CommentResponse> getCommentsByBoardId(Long boardId) {
        return commentMapper.selectCommentsByBoardId(boardId);
    }
    
    // 3. 댓글 삭제 (Delete)
    @Transactional
    public boolean deleteComment(Long commentId, String userId) {
        // 삭제 전 DB에서 원본 댓글 데이터 꺼내오기
        CommentResponse comment = commentMapper.selectCommentById(commentId);
        
        // 댓글이 존재하지 않는 경우
        if (comment == null) return false;

        // 로그인한 사람이 댓글 작성자와 다르면 에러
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        // 검증을 통과했다면 삭제 실행
        return commentMapper.deleteComment(commentId) > 0;
    }
}
