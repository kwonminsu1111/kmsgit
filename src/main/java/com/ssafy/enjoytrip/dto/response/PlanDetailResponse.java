package com.ssafy.enjoytrip.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanDetailResponse {
    private Long detailId;
    private Long planId;  // 어떤 일정에 속해있는가?
    private Long placeId;
    private int day;  // 일차
    private int sequence;  // 일차 내 순서
}