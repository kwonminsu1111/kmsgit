package com.ssafy.enjoytrip.dto.request;

import lombok.Getter;
import lombok.Setter;

// 개별 장소 블록 데이터 세트

@Getter
@Setter
public class PlanDetailRequest {
    private Long placeId;
    private int day;           // n일차
    private int sequence;      // 일차 내에서의 순서 (드래그앤드롭 결과값)
}