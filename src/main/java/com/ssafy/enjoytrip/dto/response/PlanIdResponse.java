package com.ssafy.enjoytrip.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlanIdResponse {
    private Long planId;
}

// 플랜 추가와 플랜 세부 수정에서 성공한 뒤 planId 날려주는 용