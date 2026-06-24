package com.ssafy.enjoytrip.controller;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

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

import com.ssafy.enjoytrip.dto.request.BoardCreateRequest;
import com.ssafy.enjoytrip.dto.request.BoardUpdateRequest;
import com.ssafy.enjoytrip.dto.response.BoardIdResponse;
import com.ssafy.enjoytrip.dto.response.BoardLikeResponse;
import com.ssafy.enjoytrip.dto.response.BoardResponse;
import com.ssafy.enjoytrip.model.Hashtag;
import com.ssafy.enjoytrip.service.BoardService;
import com.ssafy.enjoytrip.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Board 컨트롤러", description = "게시글 목록, 상세, 작성, 수정, 삭제 API")
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    private Long getLoginUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("loginUserId");
    }

    @Operation(summary = "게시글 리스트 보기")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getAllBoards(
            @RequestParam(required = false) String region,
            @RequestParam(required = false, defaultValue = "false") boolean myPosts,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false, defaultValue = "latest") String orderBy,
            @RequestParam(required = false, defaultValue = "false") boolean myTags,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        List<String> tagList = tags == null || tags.trim().isEmpty()
                ? null
                : Arrays.stream(tags.split(","))
                        .map(String::trim)
                        .filter(tag -> !tag.isEmpty())
                        .toList();
        List<BoardResponse> response = boardService.getAllBoards(region, orderBy, myPosts, userId, tagList, myTags);
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회가 완료되었습니다.", response));
    }

    @Operation(summary = "전체 해시태그 불러오기")
    @GetMapping("/all-tags")
    public ResponseEntity<ApiResponse<List<Hashtag>>> getAllHashtags() {
        List<Hashtag> tags = boardService.getAllHashtags();
        return ResponseEntity.ok(ApiResponse.success("요청이 성공적입니다.", tags));
    }

    @Operation(summary = "게시글 상세 보기")
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoard(
            @PathVariable Long boardId,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        BoardResponse response = boardService.getBoardById(boardId, userId);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "존재하지 않는 게시글입니다."));
        }
        return ResponseEntity.ok(ApiResponse.success("게시글 상세 조회가 완료되었습니다.", response));
    }

    @Operation(summary = "좋아요 개수 갱신")
    @PostMapping("/{boardId}/likes")
    public ResponseEntity<ApiResponse<BoardLikeResponse>> toggleLike(
            @PathVariable Long boardId,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        BoardLikeResponse response = boardService.toggleLike(boardId, userId);
        String message = response.getIsLiked()
                ? "게시글 좋아요가 등록되었습니다."
                : "게시글 좋아요가 취소되었습니다.";
        return ResponseEntity.ok(ApiResponse.success(message, response));
    }

    @Operation(summary = "게시글 작성")
    @PostMapping
    public ResponseEntity<ApiResponse<BoardIdResponse>> createBoard(
            @RequestBody BoardCreateRequest dto,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        Long boardId = boardService.createBoard(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "201",
                        "게시글이 성공적으로 등록되었습니다.",
                        new BoardIdResponse(boardId)
                ));
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteBoard(
            @PathVariable Long boardId,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        boolean isDeleted = boardService.deleteBoard(boardId, userId);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "존재하지 않는 게시글입니다."));
        }
        return ResponseEntity.ok(ApiResponse.success("게시글이 성공적으로 삭제되었습니다.", Map.of()));
    }

    @Operation(summary = "게시글 수정")
    @PatchMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardIdResponse>> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardUpdateRequest dto,
            HttpServletRequest request
    ) {
        Long userId = getLoginUserId(request);
        boolean isUpdated = boardService.updateBoard(boardId, dto, userId);
        if (!isUpdated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "존재하지 않는 게시글입니다."));
        }
        return ResponseEntity.ok(ApiResponse.success(
                "게시글이 성공적으로 수정되었습니다.",
                new BoardIdResponse(boardId)
        ));
    }
}
