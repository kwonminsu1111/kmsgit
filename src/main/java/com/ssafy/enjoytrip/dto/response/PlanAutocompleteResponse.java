package com.ssafy.enjoytrip.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanAutocompleteResponse {
    private String startDate;
    private String endDate;
    private List<String> places;  // 방문한 장소들의 리스트
}