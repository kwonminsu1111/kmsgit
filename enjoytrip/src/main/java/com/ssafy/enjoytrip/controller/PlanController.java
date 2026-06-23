package com.ssafy.enjoytrip.controller;

import java.util.HashMap;
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
import com.ssafy.enjoytrip.dto.response.PlanAutocompleteResponse;
import com.ssafy.enjoytrip.dto.response.PlanResponse;
import com.ssafy.enjoytrip.service.PlanService;
import com.ssafy.enjoytrip.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Plan 컨트롤러", description = "여행 플래너 카드 CRUD 및 일차별 상세 동선 제어 컨트롤러")
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    
    private Long getLoginUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("loginUserId");
    }

    @Operation(summary = "내 여행 일정 목록 조회", description = "status 파라미터(ongoing/completed)에 따라 내 일정 카드를 가져옵니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getPlanList(
            @RequestParam(required = false, defaultValue = "ongoing") String status,
            HttpServletRequest request
    ) {
    	Long userId = (Long) getLoginUserId(request);
        List<PlanResponse> plans = planService.getPlans(userId, status);
        return ResponseEntity.ok(ApiResponse.success("여행 계획 목록 조회 성공", plans));
    }
    
    @Operation(summary = "여행 일정에 새로운 장소 코스 추가", description = "요청 경로의 planId와 바디의 placeId를 엮어 상세 일정을 추가합니다. DB에 장소가 없으면 자동 JIT 생성이 발동합니다.")
    @PostMapping("/{planId}/add")
    public ResponseEntity<Map<String, Object>> addPlaceToPlan(
            @PathVariable Long planId,
            @RequestBody PlanDetailRequest dto,
            HttpServletRequest request
    ) {
    	Long userId = getLoginUserId(request);
        
        boolean isSuccess = planService.addPlaceToPlan(planId, userId, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", isSuccess);
        response.put("code", isSuccess ? "201" : "400");
        response.put("message", isSuccess ? "장소가 여행 일정에 추가되었습니다." : "일정 코스 추가에 실패했습니다.");

        return isSuccess ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                		 : ResponseEntity.badRequest().body(response);
    }
    
    @Operation(summary = "새 여행 일정 생성", description = "[화면 1]에서 사용하며, 시작일이 과거인 날짜면 생성 자체를 차단합니다.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> createPlan(
            @RequestBody PlanCreateRequest dto,
            HttpServletRequest request
    ) {
    	Long userId = getLoginUserId(request);
        Long planId = planService.createPlanTemplate(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("여행 계획 생성 성공", planId));
    }

    @Operation(summary = "계획 카드 제목 변경", description = "[화면 1] 목록의 더보기 메뉴에서 제목만 단독으로 고칠 때 호출합니다.")
    @PatchMapping("/{planId}")
    public ResponseEntity<ApiResponse<String>> updateTitle(
            @PathVariable Long planId,
            @RequestParam String title,
            HttpServletRequest request
    ) {
    	Long userId = getLoginUserId(request);
        boolean isUpdated = planService.modifyPlanTitle(planId, userId, title);

        if (isUpdated) {
            return ResponseEntity.ok(ApiResponse.success("계획 제목 변경 성공", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("존재하지 않는 계획입니다."));
    }

    @Operation(summary = "계획 카드 영구 삭제", description = "[화면 1]에서 카드를 날릴 때 자식 장소 블록까지 연쇄 파괴(CASCADE)합니다.")
    @DeleteMapping("/{planId}")
    public ResponseEntity<ApiResponse<String>> deletePlan(
            @PathVariable Long planId,
            HttpServletRequest request
    ) {
    	Long userId = getLoginUserId(request);
        boolean isDeleted = planService.removePlan(planId, userId);

        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success("여행 계획 삭제 성공", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("존재하지 않는 계획입니다."));
    }

    @Operation(summary = "계획 세부 수정", description = "수정된 종료일이 시작일보다 빠르면 400 에러를 뿜어냅니다.")
    @PatchMapping("/{planId}/detail")
    public ResponseEntity<ApiResponse<String>> saveFullPlan(
            @PathVariable Long planId,
            @RequestBody PlanSaveRequest dto,
            HttpServletRequest request
    ) {
    	Long userId = getLoginUserId(request);
        planService.saveFullPlanRoute(planId, userId, dto);
        return ResponseEntity.ok(ApiResponse.success("여행 계획 저장 완료", null));
    }
    
    @Operation(summary = "게시글 작성 양식 일정 자동완성 패키지 로드", description = "드롭다운에서 완료된 계획 선택 시 폼을 자동완성할 부모/자식 토탈 에셋을 반환합니다.")
    @GetMapping("/{planId}/autocomplete")
    public ResponseEntity<ApiResponse<PlanAutocompleteResponse>> getPlanAutocompleteForBoard(@PathVariable Long planId) {
        PlanAutocompleteResponse autocompleteData = planService.getPlanAutocompleteData(planId);
        return ResponseEntity.ok(ApiResponse.success("게시글 컴포넌트 자동완성용 데이터 패키징 성공", autocompleteData));
    }
}