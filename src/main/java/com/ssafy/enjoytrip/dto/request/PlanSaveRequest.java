package com.ssafy.enjoytrip.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanSaveRequest {
    private String startDate; // 플래너 내부에서 변경 가능한 여행 시작일 (YYYY-MM-DD)
    private String endDate;   // 플래너 내부에서 변경 가능한 여행 종료일 (YYYY-MM-DD)
    private List<PlanDetailRequest> details; // 일차별 장소 블록 리스트 일괄 수집
}