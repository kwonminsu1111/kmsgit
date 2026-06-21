package com.ssafy.enjoytrip.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanDetail {
    private Long id;
    private Long placeId; // 🎯 카카오 place_id와 다이렉트 매핑
    private Long planId;
    private int sequence;
    private int day;
}