package com.ssafy.enjoytrip.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCreateRequest {
    private Long planId;
    private String title;
    private String content;
    private String region;
    private String startDate;
    private String endDate;
    private List<Integer> hashtags;
}
