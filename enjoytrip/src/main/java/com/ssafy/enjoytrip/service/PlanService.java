package com.ssafy.enjoytrip.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.enjoytrip.dto.request.PlanCreateRequest;
import com.ssafy.enjoytrip.dto.request.PlanDetailRequest;
import com.ssafy.enjoytrip.dto.request.PlanSaveRequest;
import com.ssafy.enjoytrip.dto.response.PlanAutocompleteResponse;
import com.ssafy.enjoytrip.dto.response.PlanDetailResponse;
import com.ssafy.enjoytrip.dto.response.PlanResponse;
import com.ssafy.enjoytrip.mapper.PlanMapper;
import com.ssafy.enjoytrip.model.Plan;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanMapper planMapper;

    @Transactional
    public boolean addPlaceToPlan(Long planId, String userId, PlanDetailRequest request) {
    	validatePlanOwner(planId, userId);
    	
        // 1. 카카오 place_id가 우리 DB(Attractions)에 있는지 은밀하게 검사합니다.
        int isExist = planMapper.checkAttractionExist(request.getPlaceId());
        
        // 2. 없으면 외래키(FK) 에러 방지를 위해 빈 껍데기를 즉시 만들어줍니다.
        if (isExist == 0) {
            // 카카오에서 넘어온 placeId와 카테고리를 이용해 최소한의 데이터만 저장!
            planMapper.insertBlankAttraction(request.getPlaceId());
        }
        
        // 3. 장소 그릇이 준비되었으니 일정 테이블(Plans_Details)에 인서트!
        return planMapper.insertPlanDetail(planId, request) > 0;
    }
    
    // 1. 내 일정 목록 로드
    public List<PlanResponse> getPlans(String userId, String status) {
        String queryStatus = "completed".equalsIgnoreCase(status) ? "COMPLETED" : "ONGOING";
        return planMapper.selectPlansByUserIdAndStatus(userId, queryStatus);
    }

    // 2. 일정카드 추가 버튼 클릭 시 
    @Transactional
    public Long createPlanTemplate(String userId, PlanCreateRequest dto) {
        LocalDate startDate = LocalDate.parse(dto.getStartDate());
        LocalDate endDate = LocalDate.parse(dto.getEndDate());
        LocalDate today = LocalDate.now();

        // 과거 날짜 원천 차단
        if (startDate.isBefore(today)) {
            throw new IllegalArgumentException("여행 시작일은 과거 날짜로 설정할 수 없습니다.");
        }
        
        // 종료일이 시작일보다 과거라면 컷!
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("여행 종료일은 시작일보다 빠를 수 없습니다.");
        }

        Plan plan = new Plan();
        plan.setUserId(userId);
        plan.setTitle(dto.getTitle());
        plan.setStartDate(dto.getStartDate());
        plan.setEndDate(dto.getEndDate());
        
        planMapper.insertPlanMaster(plan);
        return plan.getId();
    }

    // 3. ... 메뉴 -> 제목 수정
    @Transactional
    public boolean modifyPlanTitle(Long planId, String userId, String newTitle) {
    	validatePlanOwner(planId, userId);
        return planMapper.updatePlanTitle(planId, newTitle) > 0;
    }

    // 4. ... 메뉴 -> 카드 완전 삭제
    @Transactional
    public boolean removePlan(Long planId, String userId) {
    	validatePlanOwner(planId, userId);
    	
        // 자식 테이블(Plans_Details)의 종속 장소 블록들 선행 파괴
        planMapper.deletePlanDetailsByPlanId(planId);
        // 부모 테이블 카드 최후 삭제
        return planMapper.deletePlanMaster(planId) > 0;
    }

    // 5. 장소 정보 리스트 조회
    public List<PlanDetailResponse> getPlanDetails(Long planId) {
        return planMapper.selectPlanDetailsByPlanId(planId);
    }

    // 6. 플래너 내부에서 수정할 때도 필터링
    @Transactional
    public boolean saveFullPlanRoute(Long planId, String userId, PlanSaveRequest dto) {
    	validatePlanOwner(planId, userId);
    	
    	String currentStatus = planMapper.selectPlanStatusById(planId);
        if (currentStatus == null) {
            throw new IllegalArgumentException("존재하지 않거나 이미 삭제된 여행 계획입니다.");
        }
    	
        // 이미 완료된(COMPLETED) 일정은 날짜 수정 불가
        if ("COMPLETED".equals(currentStatus)) {
            System.out.println("======> [UX 세이프 가드] 완료된 일정의 기간은 고정하고, 내부 장소 기록 수정만 허용합니다.");
        }
        
        // 아직 진행 중(ONGOING)인 일정일 때
        else {
            LocalDate startDate = LocalDate.parse(dto.getStartDate());
            LocalDate endDate = LocalDate.parse(dto.getEndDate());
            LocalDate today = LocalDate.now();

            if (startDate.isBefore(today)) {
                throw new IllegalArgumentException("여행 시작일은 과거 날짜로 설정하거나 수정할 수 없습니다.");
            }
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("여행 종료일은 시작일보다 빠를 수 없습니다.");
            }

            // 진행 중일 때만 큰 틀의 부모 날짜 메타 정보를 업데이트합니다.
            planMapper.updatePlanMaster(planId, dto);
        }

        // 하위 상세 일정 리셋 후 재생성
        planMapper.deletePlanDetailsByPlanId(planId);
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (PlanDetailRequest detail : dto.getDetails()) {
                planMapper.insertPlanDetail(planId, detail);
            }
        }
        
        return true;
    }
    
    // 7. 글 작성 자동완성을 위한 불러오기
    public PlanAutocompleteResponse getPlanAutocompleteData(Long planId) {
        // 1. 부모 일정 카드 정보 조회
        Plan plan = planMapper.selectPlanMasterById(planId);
        
        if (plan == null) {
            throw new IllegalArgumentException("존재하지 않거나 삭제된 일정입니다.");
        }

        // 2. 일차/순서대로 정렬된 하위 장소 배열 조회 (기존에 만든 검증된 매퍼 재활용!)
        List<PlanDetailResponse> details = planMapper.selectPlanDetailsByPlanId(planId);

        // 3. 반환할 프레임 생성
        PlanAutocompleteResponse response = new PlanAutocompleteResponse();
        
        response.setStartDate(plan.getStartDate());
        response.setEndDate(plan.getEndDate());
        
        // 4. 카카오 장소 ID 번호들만 담아줌
        List<String> placeIdList = new ArrayList<>();
        for (PlanDetailResponse detail : details) {
            placeIdList.add(String.valueOf(detail.getPlaceId())); // Long을 String으로 변환해서 담기
        }
        response.setPlaces(placeIdList);
        
        return response;
    }
    
    private void validatePlanOwner(Long planId, String userId) {
        Plan plan = planMapper.selectPlanMasterById(planId);
        
        if (plan == null) {
            throw new IllegalArgumentException("존재하지 않는 여행 계획입니다.");
        }
        if (!userId.equals(plan.getUserId())) {
            throw new IllegalArgumentException("본인의 여행 계획만 수정할 수 있습니다.");
        }
    }
}