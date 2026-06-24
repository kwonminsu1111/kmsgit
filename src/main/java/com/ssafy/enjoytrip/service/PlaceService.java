package com.ssafy.enjoytrip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.enjoytrip.dto.request.ReviewCreateRequest;
import com.ssafy.enjoytrip.dto.response.MainReviewResponse;
import com.ssafy.enjoytrip.dto.response.PlaceDetailResponse;
import com.ssafy.enjoytrip.mapper.PlaceMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceMapper placeMapper;

    public PlaceDetailResponse getPlaceDetail(Long placeId, Long currentUserId) {
        // 1. 리뷰 테이블을 집계하여 장소 고유 식별자 및 평균 평점 스캔
        PlaceDetailResponse detail = placeMapper.selectPlaceDetailBase(placeId);
        
        // 2. 해당 장소 하위 리뷰 리스트 최신순 닉네임 조인 스캔
        List<MainReviewResponse> reviews = placeMapper.selectReviewsByPlaceId(placeId);
        
        // 3. 본인 소유 확인 (isOwner) 도장 찍기 로직
        for (MainReviewResponse review : reviews) {
            if (review.getUserId() != null && review.getUserId().equals(currentUserId)) {
                review.setIsOwner(true);
            }
        }
        
        detail.setReviews(reviews);
        return detail;
    }
    
    // 리뷰 작성
    @Transactional
    public Long createReview(Long placeId, Long userId, ReviewCreateRequest dto) {
        
        // 별점이 1점~5점 범위를 벗어나면 DB까지 가지도 못하게 컷
        if (dto.getRate() < 1 || dto.getRate() > 5) {
            throw new IllegalArgumentException("별점 점수는 1점에서 5점 사이로만 줄 수 있습니다.");
        }
        
        // 1. 우리 DB에 이 장소(placeId)가 등록된 적이 있는지 탐색
        int isExist = placeMapper.checkAttractionExist(placeId);
        
        // 2. 처음 리뷰가 달리는 장소라면 백엔드가 그릇(Insert)을 먼저 생성.
        if (isExist == 0) {
            placeMapper.insertBlankAttraction(placeId);
        }
        
        // 3. 안전하게 외래키를 참조하여 Reviews 테이블에 인서트
        int inserted = placeMapper.insertReview(placeId, userId, dto);
        if (inserted == 0) {
            throw new IllegalStateException("리뷰 등록에 실패했습니다.");
        }

        return placeMapper.selectLastInsertId();
    }

    // 리뷰 삭제
    @Transactional
    public boolean deleteReview(Long reviewId, Long currentUserId) {
    	// 1. DB에서 해당 리뷰를 누가 작성했는지 검사
    	Long writerId = placeMapper.selectReviewWriterId(reviewId);

        if (writerId == null) {
            throw new IllegalArgumentException("존재하지 않는 리뷰입니다.");
        }
        
        // 2. 현재 토큰의 유저와 작성자가 일치하는지 대조
        if (!writerId.equals(currentUserId)) {
            throw new IllegalStateException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }
        
        // 3. 검증 통과 시 안전하게 삭제
        return placeMapper.deleteReview(reviewId) > 0;
    }
}
