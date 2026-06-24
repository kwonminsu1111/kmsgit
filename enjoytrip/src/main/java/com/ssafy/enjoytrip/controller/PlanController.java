package com.ssafy.enjoytrip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.enjoytrip.dto.request.PlanCreateRequest;
import com.ssafy.enjoytrip.dto.request.PlanDetailRequest;
import com.ssafy.enjoytrip.dto.request.PlanSaveRequest;
import com.ssafy.enjoytrip.dto.request.PlanTitleUpdateRequest;
import com.ssafy.enjoytrip.dto.response.PlanAutocompleteResponse;
import com.ssafy.enjoytrip.dto.response.PlanDetailViewResponse;
import com.ssafy.enjoytrip.dto.response.PlanIdResponse;
import com.ssafy.enjoytrip.dto.response.PlanResponse;
import com.ssafy.enjoytrip.service.PlanService;
import com.ssafy.enjoytrip.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Plan 컨트롤러", description = "여행 계획 CRUD 및 상세 일정 API")
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    private Long getLoginUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("loginUserId");
    }

    @Operation(summary = "여행 계획 리스트 보기")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getPlanList(
            @RequestParam(required = false, defaultValue = "ongoing") String status,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        List<PlanResponse> plans = planService.getPlans(userId, status);
        return ResponseEntity.ok(ApiResponse.success("여행 계획 리스트 조회 성공", plans));
    }

    @Operation(summary = "테스트용: 여행 일정에 장소 추가")
    @PostMapping("/{planId}/add")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addPlaceToPlan(
            @PathVariable Long planId,
            @RequestBody PlanDetailRequest dto,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        planService.addPlaceToPlan(planId, userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("201", "장소가 여행 일정에 추가되었습니다.", Map.of()));
    }

    @Operation(summary = "여행 계획 추가")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<PlanIdResponse>> createPlan(
            @RequestBody PlanCreateRequest dto,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        Long planId = planService.createPlanTemplate(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "201",
                        "여행 계획이 성공적으로 등록되었습니다.",
                        new PlanIdResponse(planId)
                ));
    }

    @Operation(summary = "여행계획 수정(제목 수정)")
    @PatchMapping("/{planId}")
    public ResponseEntity<ApiResponse<Void>> updateTitle(
            @PathVariable Long planId,
            @RequestBody PlanTitleUpdateRequest dto,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        String newTitle = dto != null ? dto.getTitle() : null;
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("수정할 여행 계획 제목을 입력해주세요.");
        }

        planService.modifyPlanTitle(planId, userId, newTitle.trim());
        return ResponseEntity.ok(ApiResponse.success("여행 기본 정보가 성공적으로 수정되었습니다.", null));
    }

    @Operation(summary = "여행 계획 삭제")
    @DeleteMapping("/{planId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deletePlan(
            @PathVariable Long planId,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        planService.removePlan(planId, userId);
        return ResponseEntity.ok(ApiResponse.success("여행 계획 삭제 성공", Map.of()));
    }

    @Operation(summary = "여행 계획 상세 조회")
    @GetMapping("/{planId}/detail")
    public ResponseEntity<ApiResponse<PlanDetailViewResponse>> getPlanDetail(
            @PathVariable Long planId,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        PlanDetailViewResponse response = planService.getPlanDetail(planId, userId);
        return ResponseEntity.ok(ApiResponse.success("여행 계획 상세 조회 성공", response));
    }

    @Operation(summary = "여행 계획 상세 수정")
    @PatchMapping("/{planId}/detail")
    public ResponseEntity<ApiResponse<PlanIdResponse>> saveFullPlan(
            @PathVariable Long planId,
            @RequestBody PlanSaveRequest dto,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        planService.saveFullPlanRoute(planId, userId, dto);
        return ResponseEntity.ok(ApiResponse.success(
                "여행 계획이 성공적으로 수정되었습니다.",
                new PlanIdResponse(planId)
        ));
    }

    @Operation(summary = "여행 계획 데이터 추출 - 게시판 연동")
    @GetMapping("/{planId}/autocomplete")
    public ResponseEntity<ApiResponse<PlanAutocompleteResponse>> getPlanAutocompleteForBoard(@PathVariable Long planId) {
        PlanAutocompleteResponse autocompleteData = planService.getPlanAutocompleteData(planId);
        return ResponseEntity.ok(ApiResponse.success("게시글 컴포넌트 자동완성용 데이터 패키징 성공", autocompleteData));
    }
}
