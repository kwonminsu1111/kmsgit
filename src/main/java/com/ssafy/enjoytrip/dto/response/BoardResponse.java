package com.ssafy.enjoytrip.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardResponse {
	private Long id;
	private String userId;
	private Long planId;
	private String title;
	private String content;
	private LocalDateTime createdAt;
	private int hit;
	private int likeCount;
	private boolean isLiked;
	private int commentCount;
	private String region;
	private String startDate;
	private String endDate;
	private List<String> hashtags;
}
