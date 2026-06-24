package com.ssafy.enjoytrip.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.enjoytrip.dto.request.ReviewCreateRequest;
import com.ssafy.enjoytrip.dto.response.PlaceDetailResponse;
import com.ssafy.enjoytrip.service.PlaceService;
import com.ssafy.enjoytrip.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "메인페이지 & 장소 리뷰 컨트롤러", description = "메인화면 카카오맵 연동 및 사이드바 후기 관제탑")
@RestController
@RequestMapping("/main/searchplace") // 🎯 명세서 표준 기본 URL 주소 전면 준수
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    private Long getLoginUserId(HttpServletRequest request) {
	    return (Long) request.getAttribute("loginUserId");
	}
    
    @Operation(summary = "장소 상세 조회 (평점 + 리뷰 목록)", description = "지도에서 특정 마커 클릭 시 해당 장소의 평균 별점과 리뷰 목록 세트를 반환합니다.")
    @GetMapping("/{placeId}")
    public ResponseEntity<ApiResponse<PlaceDetailResponse>> getPlaceDetail(
            @PathVariable Long placeId,
            HttpServletRequest request
    ) {
    	Long userId = (Long) getLoginUserId(request);
        PlaceDetailResponse detail = placeService.getPlaceDetail(placeId, userId);
        return ResponseEntity.ok(ApiResponse.success("장소 상세 조회 성공", detail));
    }

    @Operation(summary = "특정 장소에 새로운 후기(리뷰) 등록", description = "장소 ID를 받아 리뷰를 작성합니다. 별점이 1~5점 사이가 아니거나 첫 등록 장소일 경우 예외 가드가 가동합니다.")
    @PostMapping("/{placeId}/reviews")
    public ResponseEntity<Map<String, Object>> createReview(
            @PathVariable Long placeId,
            @RequestBody ReviewCreateRequest dto,
            HttpServletRequest request
    ) {
    	Long userId = getLoginUserId(request);
        boolean isSuccess = placeService.createReview(placeId, userId, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", isSuccess);
        response.put("code", isSuccess ? "201" : "400");
        response.put("message", isSuccess ? "리뷰가 등록되었습니다." : "리뷰 등록에 실패했습니다.");

        return isSuccess
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.badRequest().body(response);
    }

    @Operation(summary = "작성자 본인의 리뷰 삭제", description = "리뷰 고유 ID를 대조하여 작성자가 본인일 경우 즉시 물리 폭파를 집도합니다.")
    @DeleteMapping("/{placeId}/reviews/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(
            @PathVariable Long reviewId,
            HttpServletRequest request
    ) {
    	Long userId = getLoginUserId(request);
        placeService.deleteReview(reviewId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", true);
        response.put("code", "200");
        response.put("message", "리뷰가 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}