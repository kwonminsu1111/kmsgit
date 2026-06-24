package com.ssafy.enjoytrip.controller;

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

@Tag(name = "Main 컨트롤러", description = "지도 장소 상세 조회 및 리뷰 관리 API")
@RestController
@RequestMapping("/main/searchplace")
@RequiredArgsConstructor
public class PlaceController {

    private static final String PLACE_DETAIL_SUCCESS_MESSAGE = "장소 상세 정보 조회가 성공적으로 완료되었습니다.";
    private static final String REVIEW_CREATE_SUCCESS_MESSAGE = "리뷰가 성공적으로 등록되었습니다.";
    private static final String REVIEW_DELETE_SUCCESS_MESSAGE = "리뷰가 성공적으로 삭제되었습니다.";

    private final PlaceService placeService;

    private Long getLoginUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("loginUserId");
    }

    @Operation(summary = "지도 상세 검색", description = "장소의 평균 별점과 리뷰 목록을 조회합니다.")
    @GetMapping("/{placeId}")
    public ResponseEntity<ApiResponse<PlaceDetailResponse>> getPlaceDetail(
            @PathVariable Long placeId,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        PlaceDetailResponse detail = placeService.getPlaceDetail(placeId, userId);
        return ResponseEntity.ok(ApiResponse.success(PLACE_DETAIL_SUCCESS_MESSAGE, detail));
    }

    @Operation(summary = "리뷰 작성", description = "장소에 리뷰를 등록합니다.")
    @PostMapping("/{placeId}/reviews")
    public ResponseEntity<ApiResponse<Map<String, Long>>> createReview(
            @PathVariable Long placeId,
            @RequestBody ReviewCreateRequest dto,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        Long reviewId = placeService.createReview(placeId, userId, dto);
        ApiResponse<Map<String, Long>> response = ApiResponse.success(
                "201",
                REVIEW_CREATE_SUCCESS_MESSAGE,
                Map.of("reviewId", reviewId)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "리뷰 삭제", description = "본인이 작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{placeId}/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteReview(
            @PathVariable Long placeId,
            @PathVariable Long reviewId,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        placeService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success(REVIEW_DELETE_SUCCESS_MESSAGE, Map.of()));
    }
}
