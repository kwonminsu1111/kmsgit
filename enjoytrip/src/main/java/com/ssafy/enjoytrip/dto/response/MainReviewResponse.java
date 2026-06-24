package com.ssafy.enjoytrip.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainReviewResponse {

    private Long reviewId;
    private String userNickname;
    private int rate;
    private String content;
    private String createdAt;

    @JsonIgnore
    private Long userId;

    @Schema(name = "isOwner", description = "로그인 유저가 리뷰를 작성한 사람인가?")
    @JsonProperty("isOwner")
    @Getter(AccessLevel.NONE)
    private boolean isOwner;

    public boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }
}
