package com.ssafy.enjoytrip.dto.response;

import lombok.Getter;
import lombok.Setter;

// 키워드 검색 결과 단건 조회

@Getter
@Setter
public class PlaceSearchResponse {
    private Long placeId;
    private String placeName;
    private String category;
    private double latitude;
    private double longitude;
}