package com.ssafy.enjoytrip.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.enjoytrip.dto.request.PlanDetailRequest;
import com.ssafy.enjoytrip.dto.request.PlanSaveRequest;
import com.ssafy.enjoytrip.dto.response.PlanDetailResponse;
import com.ssafy.enjoytrip.dto.response.PlanResponse;
import com.ssafy.enjoytrip.model.Plan;

@Mapper
public interface PlanMapper {
    // 1. 내 여행 계획 목록 조회 (ongoing / completed 필터링)
    List<PlanResponse> selectPlansByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    
    // 2. 새 계획 마스터 껍데기 생성 (useGeneratedKeys 마법)
    int insertPlanMaster(Plan plan);
    
    // 3. 계획 내부 날짜 동기화 갱신
    int updatePlanMaster(@Param("planId") Long planId, @Param("dto") PlanSaveRequest dto);
    
    // 4. 계획 제목만 단독 수정 (...)
    int updatePlanTitle(@Param("planId") Long planId, @Param("title") String title);
    
    // 5. 계획 카드 통째로 삭제 (...)
    int deletePlanMaster(Long planId);
    
    // 현재 계획의 status 판별 (진행중 or 완료) -> 날짜 변경 가능 여부 판별
    String selectPlanStatusById(Long planId);
    
    // 게시물 양식 자동완성 - 플랜 부모데이터 조회
    Plan selectPlanMasterById(Long planId);
    
    // ==========================================
    // 플래너 편집창 (화면2) 연동 영역
    // ==========================================
    
    // 6. 수정 시 날짜 변동에 따른 동적 상태 직접 변경
    int updatePlanStatusDirectly(@Param("planId") Long planId, @Param("status") String status);
    
    // 7. 매일 밤 자정 자동 완료 처리용
    int updatePlanStatusAutomatically();
    
    // 8. 특정 계획의 하위 장소 블록 배열 정렬 조회
    List<PlanDetailResponse> selectPlanDetailsByPlanId(Long planId);
    
    // 9. 상세 일정 데이터 물리적 리셋 (Clear)
    int deletePlanDetailsByPlanId(Long planId);
    
    // 10. 가공된 최신 정렬 블록 쓰기 (Insert)
    int insertPlanDetail(@Param("planId") Long planId, @Param("detail") PlanDetailRequest detail);
    
    // ------------------------------------------------------------------------
    // 카카오 장소id 저장
    // ------------------------------------------------------------------------
    
    // 1. 장소가 우리 DB에 있는지 확인
    int checkAttractionExist(Long placeId);
    
    // 2. 장소가 없으면 ID만 있는 껍데기 장소 인서트
    int insertBlankAttraction(@Param("placeId") Long placeId);
}