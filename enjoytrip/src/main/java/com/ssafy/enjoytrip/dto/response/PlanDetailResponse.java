package com.ssafy.enjoytrip.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanDetailResponse {
    private int sequence;
    private int day;
    private Long placeId;
}

// 플랜 안에 있는 장소 블록 하나 조회