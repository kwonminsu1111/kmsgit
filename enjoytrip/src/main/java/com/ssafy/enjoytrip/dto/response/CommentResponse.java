package com.ssafy.enjoytrip.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {
    private Long id;            // 댓글 고유 식별 번호 (PK)
    private Long boardId;       // 원본 게시글 번호
    private Long userId;      // 댓글 작성자 ID
    private String nickname;    // 댓글 작성자 닉네임 (Users 테이블과 JOIN)
    private String content;     // 댓글 내용
    private LocalDateTime createdAt; // 댓글 작성 시간
}
