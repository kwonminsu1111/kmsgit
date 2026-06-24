package com.ssafy.enjoytrip.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanResponse {
    private Long id;
    private Long userId;
    private String title;
    private String startDate;
    private String endDate;
    private String status;
    private LocalDateTime createdAt;
}