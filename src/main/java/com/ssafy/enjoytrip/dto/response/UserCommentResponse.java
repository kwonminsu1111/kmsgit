package com.ssafy.enjoytrip.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCommentResponse {
	private Long commentId;
	private Long boardId;
	private String title;
	private String content;
	private LocalDateTime createdAt;
}
