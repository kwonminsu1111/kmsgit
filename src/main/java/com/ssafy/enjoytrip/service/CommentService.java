package com.ssafy.enjoytrip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.enjoytrip.dto.response.CommentCreateResponse;
import com.ssafy.enjoytrip.dto.response.CommentResponse;
import com.ssafy.enjoytrip.mapper.CommentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    @Transactional
    public CommentCreateResponse createComment(Long boardId, Long userId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 작성해주세요.");
        }

        int inserted = commentMapper.insertComment(boardId, userId, content.trim());
        if (inserted == 0) {
            throw new IllegalStateException("댓글 등록에 실패했습니다.");
        }

        Long commentId = commentMapper.selectLastInsertId();
        CommentResponse comment = commentMapper.selectCommentById(commentId);
        return new CommentCreateResponse(
                comment.getCommentId(),
                comment.getNickname(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    public List<CommentResponse> getCommentsByBoardId(Long boardId) {
        return commentMapper.selectCommentsByBoardId(boardId);
    }

    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        CommentResponse comment = commentMapper.selectCommentById(commentId);
        if (comment == null) {
            return false;
        }
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        return commentMapper.deleteComment(commentId) > 0;
    }
}
