package com.ssafy.enjoytrip.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanCreateRequest {
    private String title;
    private String startDate;
    private String endDate;
}