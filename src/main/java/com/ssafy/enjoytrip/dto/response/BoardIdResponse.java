package com.ssafy.enjoytrip.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardIdResponse {
    private Long boardId;
}

// 게시글 작성, 게시글 수정에서 성공 시, 게시글id 반환