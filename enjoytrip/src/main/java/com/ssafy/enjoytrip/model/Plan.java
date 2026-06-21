package com.ssafy.enjoytrip.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Plan {
	private Long id;            // 여행 계획 고유 번호 (PK)
	private String userId;
    private String title;       // 여행 계획 제목
    private String startDate;   // 여행 시작일 (YYYY-MM-DD)
    private String endDate;     // 여행 종료일 (YYYY-MM-DD)
    private String status;      // ONGOING(진행중), COMPLETED(완료)
    private LocalDateTime createdAt;
}
