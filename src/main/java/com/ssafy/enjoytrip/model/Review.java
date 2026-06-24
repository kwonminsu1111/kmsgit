package com.ssafy.enjoytrip.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Review {
	private Long id;
	private Long userId;
	private Long placeId;
	private int rate;
	private String content;
	private LocalDateTime createdAt;
}
