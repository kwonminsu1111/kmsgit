package com.ssafy.enjoytrip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.enjoytrip.dto.request.UserLoginRequest;
import com.ssafy.enjoytrip.dto.request.UserSignupRequest;
import com.ssafy.enjoytrip.dto.request.UserUpdateRequest;
import com.ssafy.enjoytrip.dto.response.UserCommentResponse;
import com.ssafy.enjoytrip.dto.response.UserLikedBoardResponse;
import com.ssafy.enjoytrip.dto.response.UserLoginResponse;
import com.ssafy.enjoytrip.dto.response.UserProfileResponse;
import com.ssafy.enjoytrip.model.User;
import com.ssafy.enjoytrip.service.UserService;
import com.ssafy.enjoytrip.util.ApiResponse;
import com.ssafy.enjoytrip.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "User Controller", description = "회원가입, 로그인, 마이페이지")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String SUCCESS_MESSAGE = "요청이 성공적으로 처리되었습니다.";

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody UserSignupRequest dto) {
        userService.signup(dto);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_MESSAGE, null));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(
            @RequestBody UserLoginRequest dto,
            HttpServletRequest request
    ) {
        String email = dto == null ? null : dto.getEmail();
        String password = dto == null ? null : dto.getPassword();
        User loginUser = userService.login(email, password);

        String accessToken = jwtUtil.createAccessToken(loginUser.getId());
        String refreshToken = jwtUtil.createRefreshToken(loginUser.getId());
        UserLoginResponse userResponse = UserLoginResponse.from(loginUser, accessToken);
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken, request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiResponse.success(SUCCESS_MESSAGE, userResponse));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        ResponseCookie expiredRefreshTokenCookie = expireRefreshTokenCookie(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredRefreshTokenCookie.toString())
                .body(ApiResponse.success(SUCCESS_MESSAGE, null));
    }

    @Operation(summary = "Access Token Refresh")
    @PostMapping("/re-issue")
    public ResponseEntity<ApiResponse<Map<String, String>>> reIssue(HttpServletRequest request) {
        String refreshToken = extractRefreshToken(request);

        if (refreshToken == null || !jwtUtil.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Refresh token is missing or invalid."));
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        if (userId == null || !userService.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Refresh token is missing or invalid."));
        }

        String accessToken = jwtUtil.createAccessToken(userId);
        return ResponseEntity.ok(ApiResponse.success("Access Token Refreshed", Map.of("accessToken", accessToken)));
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        userService.deleteUser(userId);
        ResponseCookie expiredRefreshTokenCookie = expireRefreshTokenCookie(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredRefreshTokenCookie.toString())
                .body(ApiResponse.success(SUCCESS_MESSAGE, null));
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        UserProfileResponse userProfileResponse = userService.getUserInfo(userId);

        if (userProfileResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "존재하지 않는 회원입니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(SUCCESS_MESSAGE, userProfileResponse));
    }

    @Operation(summary = "회원 정보 수정")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<String>> updateMyInfo(
            @RequestBody UserUpdateRequest updateRequest,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        userService.updateUser(userId, updateRequest);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_MESSAGE, null));
    }

    @Operation(summary = "내 댓글 게시글 조회")
    @GetMapping("/me/comments")
    public ResponseEntity<ApiResponse<List<UserCommentResponse>>> getUserComments(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        List<UserCommentResponse> response = userService.getUserComments(userId);
        return ResponseEntity.ok(ApiResponse.success("내 댓글 게시글 조회 성공", response));
    }

    @Operation(summary = "좋아요 누른 게시글 조회")
    @GetMapping("/me/liked-boards")
    public ResponseEntity<ApiResponse<List<UserLikedBoardResponse>>> getUserLikedBoards(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        List<UserLikedBoardResponse> response = userService.getUserLikedBoards(userId);
        return ResponseEntity.ok(ApiResponse.success("내가 좋아요를 누른 게시글 조회 성공", response));
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken, HttpServletRequest request) {
        boolean crossSite = isCrossSiteFrontend(request);
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(crossSite)
                .sameSite(crossSite ? "None" : "Lax")
                .path("/users/re-issue")
                .maxAge(JwtUtil.REFRESH_TOKEN_EXPIRATION_TIME_MILLIS / 1000)
                .build();
    }

    private ResponseCookie expireRefreshTokenCookie(HttpServletRequest request) {
        boolean crossSite = isCrossSiteFrontend(request);
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(crossSite)
                .sameSite(crossSite ? "None" : "Lax")
                .path("/users/re-issue")
                .maxAge(0)
                .build();
    }

    private boolean isCrossSiteFrontend(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin == null || origin.isBlank()) {
            return false;
        }
        return !origin.contains("localhost") && !origin.contains("127.0.0.1");
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
