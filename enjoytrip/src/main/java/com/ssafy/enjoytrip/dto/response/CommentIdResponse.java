package com.ssafy.enjoytrip.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentIdResponse {
    private Long commentId;
}

// 댓글 삭제 성공 후, 댓글id 반환용