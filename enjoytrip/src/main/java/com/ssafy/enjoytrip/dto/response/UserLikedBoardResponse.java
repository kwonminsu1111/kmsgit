package com.ssafy.enjoytrip.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLikedBoardResponse {
    private Long boardId;         // 클릭 시 이동할 게시글 번호
    private String title;     // 게시글 제목
    private String content;   // 게시글 내용 일부
    private LocalDateTime createdAt; // 게시글 작성 날짜
}