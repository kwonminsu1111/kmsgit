package com.ssafy.enjoytrip.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

// 각 리뷰 리스트

@Getter
@Setter
public class MainReviewResponse {
    private Long reviewId;
    private String userNickname;
    private int rate;
    private String content;
    private String createdAt;
    
    @JsonIgnore
    private String userId;
    private boolean isOwner;  // 현재 로그인 사람이 이 리뷰 작성자?
}