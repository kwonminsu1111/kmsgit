package com.ssafy.enjoytrip.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardLikeResponse {

    @JsonProperty("isLiked")
    @Getter(AccessLevel.NONE)
    private boolean isLiked;

    private int likeCount;

    public boolean getIsLiked() {
        return isLiked;
    }
}

// 좋아요 토글 클릭 후, 결과값 반환 (좋아요 누름 여부, 좋아요 카운트 개수)