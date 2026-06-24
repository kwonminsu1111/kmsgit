package com.ssafy.enjoytrip.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanDetailViewResponse {
    private String startDate;
    private String endDate;
    private List<PlanDetailResponse> details;
}

// 플랜 상세 조회용