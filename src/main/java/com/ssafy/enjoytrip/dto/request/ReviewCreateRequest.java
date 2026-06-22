package com.ssafy.enjoytrip.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequest {
    private int rate;
    private String content;
}