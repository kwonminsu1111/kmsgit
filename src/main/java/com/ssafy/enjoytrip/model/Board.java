package com.ssafy.enjoytrip.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
	private Long id;
	private String userId;
	private Long planId;
	private String title;
	private String content;
	private int hit;
	private LocalDateTime createdAt;
	private int likeCount;
	private String region;
	private String startDate;
	private String endDate;
}
