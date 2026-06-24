package com.ssafy.enjoytrip.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardUpdateRequest {
    private String title;
    private String content;

    @JsonAlias("hashtags")
    private List<String> tags;
}
