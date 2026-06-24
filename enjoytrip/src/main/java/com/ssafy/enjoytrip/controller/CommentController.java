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
import com.ssafy.enjoytrip.dto.response.CommentCreateResponse;
import com.ssafy.enjoytrip.dto.response.CommentIdResponse;
import com.ssafy.enjoytrip.dto.response.CommentResponse;
import com.ssafy.enjoytrip.service.CommentService;
import com.ssafy.enjoytrip.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Comment 컨트롤러", description = "게시글 댓글 작성, 조회, 삭제 API")
@RestController
@RequestMapping("/boards/{boardId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private Long getLoginUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("loginUserId");
    }

    @Operation(summary = "댓글 작성")
    @PostMapping
    public ResponseEntity<ApiResponse<CommentCreateResponse>> createComment(
            @PathVariable Long boardId,
            @RequestBody CommentRequest dto,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        CommentCreateResponse response = commentService.createComment(boardId, userId, dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "201",
                        "댓글이 성공적으로 등록되었습니다.",
                        response
                ));
    }

    @Operation(summary = "테스트용: 댓글 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long boardId) {
        List<CommentResponse> comments = commentService.getCommentsByBoardId(boardId);
        return ResponseEntity.ok(ApiResponse.success("댓글 목록 조회 성공", comments));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentIdResponse>> deleteComment(
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        boolean isDeleted = commentService.deleteComment(commentId, userId);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "존재하지 않는 댓글입니다."));
        }
        return ResponseEntity.ok(ApiResponse.success(
                "댓글이 성공적으로 삭제되었습니다.",
                new CommentIdResponse(commentId)
        ));
    }
}
