package com.ssafy.enjoytrip.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardResponse {
    private Long boardId;
    private Long planId;
    private String nickname;
    private String title;
    private String content;
    private Integer hit;
    private Integer likeCount;
    private Integer commentCount;
    private String region;
    private String startDate;
    private String endDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private List<String> tags;
    private List<CommentResponse> comments;

    @JsonProperty("isLiked")
    @Getter(AccessLevel.NONE)
    private boolean isLiked;

    public boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
}

// 게시글 목록 , 상세 조회용
