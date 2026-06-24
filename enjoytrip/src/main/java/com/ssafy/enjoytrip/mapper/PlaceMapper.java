package com.ssafy.enjoytrip.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.enjoytrip.dto.request.ReviewCreateRequest;
import com.ssafy.enjoytrip.dto.response.MainReviewResponse;
import com.ssafy.enjoytrip.dto.response.PlaceDetailResponse;

@Mapper
public interface PlaceMapper {
	// 1. 메인페이지: 장소 고유 식별자 및 평균 평점 스캔
    PlaceDetailResponse selectPlaceDetailBase(Long placeId);
    
    // 2. 메인페이지: 해당 장소 하위 리뷰 리스트 최신순 닉네임 조인 스캔
    List<MainReviewResponse> selectReviewsByPlaceId(Long placeId);

    // 3. 해당 카카오 장소가 DB에 이미 등록되어 있는지 확인
    int checkAttractionExist(Long placeId);
    
    // 4. 장소가 없다면 외래키 에러 방지를 위해 빈 여행지 생성
    int insertBlankAttraction(Long placeId);
    
    // 5. 실제 리뷰 등록
    int insertReview(@Param("placeId") Long placeId, @Param("userId") Long userId, @Param("dto") ReviewCreateRequest dto);

    Long selectLastInsertId();
    
    // 6. 삭제 요청 시 권한 방어용: 리뷰 작성자 ID 조회
    Long selectReviewWriterId(Long reviewId);
    
    // 7. 리뷰 삭제
    int deleteReview(Long reviewId);
}
