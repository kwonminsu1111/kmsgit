package com.ssafy.enjoytrip.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCommentResponse {
    private Long boardId;
    private String title;
    private String content;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
}
