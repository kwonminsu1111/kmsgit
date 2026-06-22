package com.ssafy.enjoytrip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.enjoytrip.dto.request.BoardCreateRequest;
import com.ssafy.enjoytrip.dto.response.BoardResponse;
import com.ssafy.enjoytrip.service.BoardService;
import com.ssafy.enjoytrip.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Board 컨트롤러", description = "자유게시판 목록 조회 및 상세 보기 API")
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor // BoardService를 자동으로 주입해 줍니다.
public class BoardController {

    private final BoardService boardService;

    private String getLoginUserId(HttpServletRequest request) {
        return (String) request.getAttribute("loginUserId");
    }
    
    @Operation(summary = "전체 게시글 목록 조회", description = "데이터베이스에 등록된 모든 게시글을 최신순, 좋아요순, 방문순으로 정렬합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getAllBoards(
    		@RequestParam(required = false) String region,
    		@RequestParam(required = false, defaultValue = "latest") String orderBy,
    		@RequestParam(required = false, defaultValue = "false") boolean myPosts,
    		@RequestParam(required = false) List<String> tags,
    		HttpServletRequest request
    ) {
    	String userId = getLoginUserId(request);
    	
        List<BoardResponse> response = boardService.getAllBoards(region, orderBy, myPosts, userId, tags);
        return ResponseEntity.ok(ApiResponse.success("전체 게시글 목록 조회 성공", response));
    }
    
    @Operation(summary = "현재 로그인한 유저의 해시태그 조회", description = "내 태그 불러오기 버튼 클릭 시 활성화할 유저의 고정 태그 목록을 반환합니다.")
    @GetMapping("/my-tags")
    public ResponseEntity<ApiResponse<List<String>>> getMyTags(
    		HttpServletRequest request
    ) {
    	String userId = getLoginUserId(request);
    	
        List<String> userTags = boardService.getUserHashtags(userId);
        return ResponseEntity.ok(ApiResponse.success("유저 해시태그 조회 성공", userTags));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 고유 번호(ID)와 특정 유저의 좋아요 여부도 확인 가능")
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoard(
            @PathVariable Long boardId,
            HttpServletRequest request
    ) {
    	String userId = getLoginUserId(request);
        BoardResponse response = boardService.getBoardById(boardId, userId);
        
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.success("게시글 상세 조회 성공", response));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("존재하지 않는 게시글입니다."));
    }
    
    @Operation(summary = "게시글 좋아요 토글", description = "게시글에 좋아요를 등록하거나 취소합니다. 최종 좋아요 완료 여부(true/false)를 반환합니다.")
    @PostMapping("/{boardId}/likes")
    public ResponseEntity<ApiResponse<Boolean>> toggleLike(
            @PathVariable Long boardId,
            HttpServletRequest request
    ) {
    	String userId = getLoginUserId(request);
        boolean isLiked = boardService.toggleLike(boardId, userId);
        return ResponseEntity.ok(ApiResponse.success(isLiked ? "좋아요 등록 성공" : "좋아요 취소 성공", isLiked));
    }
    
    // 게시글 작성
    @Operation(summary = "새 게시글 작성", description = "제목, 내용, 지역, 글쓴이 정보를 받아 새로운 게시글을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createBoard(
    		@RequestBody BoardCreateRequest dto,
    		HttpServletRequest request) {
    	
    	String userId = getLoginUserId(request);
        boolean isSuccess = boardService.createBoard(dto, userId);

        if (isSuccess) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("게시글 등록 완료", "등록 성공"));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("게시글 등록 중 서버 오류가 발생했습니다."));
    }
    
    // 게시글 삭제
    @Operation(summary = "게시글 삭제", description = "게시글 고유 번호(ID)를 사용하여 특정 게시글을 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<String>> deleteBoard(
            @PathVariable Long boardId,
            HttpServletRequest request
    ) {
    	String userId = getLoginUserId(request);
        boolean isDeleted = boardService.deleteBoard(boardId, userId);

        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success("게시글 삭제 성공", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("존재하지 않는 게시글입니다."));
    }
    
    // 게시글 수정
    @Operation(summary = "게시글 수정", description = "게시글 고유 번호(ID)와 수정된 내용을 받아 특정 게시글을 업데이트합니다.")
    @PatchMapping("/{boardId}")
    public ResponseEntity<ApiResponse<String>> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardCreateRequest dto,
            HttpServletRequest request
    ) {
    	String userId = getLoginUserId(request);
        boolean isUpdated = boardService.updateBoard(boardId, dto, userId);

        if (isUpdated) {
            return ResponseEntity.ok(ApiResponse.success("게시글 수정 성공", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("존재하지 않는 게시글입니다."));
    }
}