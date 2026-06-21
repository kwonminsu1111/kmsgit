package com.ssafy.enjoytrip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.enjoytrip.dto.request.CommentRequest;
import com.ssafy.enjoytrip.dto.response.CommentResponse;
import com.ssafy.enjoytrip.service.CommentService;
import com.ssafy.enjoytrip.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Comment 컨트롤러", description = "게시글 하위 댓글의 생성, 조회, 삭제 API")
@RestController
@RequestMapping("/api/boards/{boardId}/comments")
@RequiredArgsConstructor
public class CommentController {
	
	private final CommentService commentService;
	
	private String getLoginUserId(HttpServletRequest request) {
	    return (String) request.getAttribute("loginUserId");
	}
	
	@Operation(summary = "댓글 작성", description = "게시글 고유 번호와 댓글을 받아 새 댓글을 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createComment(
            @PathVariable Long boardId,
            @RequestBody CommentRequest dto,
            HttpServletRequest request
    ) {
		String userId = getLoginUserId(request);
	    boolean isSuccess = commentService.createComment(boardId, userId, dto.getContent());

	    if (isSuccess) {
	        return ResponseEntity.status(HttpStatus.CREATED)
	                .body(ApiResponse.success("댓글 등록 성공", null));
	    }

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(ApiResponse.error("댓글 등록에 실패했습니다."));
    }
	
	@Operation(summary = "댓글 목록 조회", description = "특정 게시글에 달린 모든 댓글 목록을 순서대로 가져옴")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long boardId) {
        List<CommentResponse> comments = commentService.getCommentsByBoardId(boardId);
        return ResponseEntity.ok(ApiResponse.success("댓글 목록 조회 성공", comments));
    }
	
	@Operation(summary = "댓글 삭제", description = "댓글의 고유 번호(commentId)를 사용해 작성자 본인 확인 후 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {
		String userId = getLoginUserId(request);
	    boolean isDeleted = commentService.deleteComment(commentId, userId);

	    if (isDeleted) {
	        return ResponseEntity.ok(ApiResponse.success("댓글 삭제 성공", null));
	    }

	    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(ApiResponse.error("존재하지 않는 댓글입니다."));
    }
}
