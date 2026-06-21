package com.ssafy.enjoytrip.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

// 각 장소 정보 : 별점의 평균, 장소의 리뷰 리스트

@Getter
@Setter
public class PlaceDetailResponse {
    private Long placeId;
    private double rateAvg; // 평균은 실수
    private List<MainReviewResponse> reviews;
}