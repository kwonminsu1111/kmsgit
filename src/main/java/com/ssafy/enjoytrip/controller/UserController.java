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

@Tag(name = "User 컨트롤러", description = "User 회원가입, 로그인, 프로필 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody UserSignupRequest dto) {
        userService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입 성공", "가입 완료"));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@RequestBody UserLoginRequest dto) {
        User loginUser = userService.login(dto.getEmail(), dto.getPassword());

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("아이디 또는 비밀번호가 일치하지 않습니다."));
        }

        String accessToken = jwtUtil.createAccessToken(loginUser.getId());
        String refreshToken = jwtUtil.createRefreshToken(loginUser.getId());
        UserLoginResponse userResponse = UserLoginResponse.from(loginUser, accessToken);
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiResponse.success("로그인이 성공적으로 완료되었습니다.", userResponse));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        ResponseCookie expiredRefreshTokenCookie = expireRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredRefreshTokenCookie.toString())
                .body(ApiResponse.success("로그아웃이 성공적으로 완료되었습니다.", "로그아웃 완료"));
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

        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 성공적으로 완료되었습니다.", "탈퇴 완료"));
    }

    @Operation(summary = "유저 프로필 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        UserProfileResponse userProfileResponse = userService.getUserInfo(userId);

        if (userProfileResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("존재하지 않는 회원입니다."));
        }

        return ResponseEntity.ok(ApiResponse.success("회원 정보 조회가 성공적으로 완료되었습니다.", userProfileResponse));
    }

    @Operation(summary = "유저 정보 수정")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<String>> updateMyInfo(
            @RequestBody UserUpdateRequest updateRequest,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        userService.updateUser(userId, updateRequest);
        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정이 성공적으로 완료되었습니다.", "수정 완료"));
    }

    @Operation(summary = "유저 댓글 조회")
    @GetMapping("/me/comments")
    public ResponseEntity<ApiResponse<List<UserCommentResponse>>> getUserComments(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        List<UserCommentResponse> response = userService.getUserComments(userId);
        return ResponseEntity.ok(ApiResponse.success("내가 작성한 댓글 목록 조회가 성공적으로 완료되었습니다.", response));
    }

    @Operation(summary = "유저가 좋아요 누른 게시글 조회")
    @GetMapping("/me/liked-boards")
    public ResponseEntity<ApiResponse<List<UserLikedBoardResponse>>> getUserLikedBoards(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        List<UserLikedBoardResponse> response = userService.getUserLikedBoards(userId);
        return ResponseEntity.ok(ApiResponse.success("내가 좋아요 한 게시글 목록 조회가 성공적으로 완료되었습니다.", response));
    }

    // RT 생성
    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/users/re-issue")
                .maxAge(JwtUtil.REFRESH_TOKEN_EXPIRATION_TIME_MILLIS / 1000)
                .build();
    }

    // RT 만료 처리
    private ResponseCookie expireRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/users/re-issue")
                .maxAge(0)
                .build();
    }

    // 쿠키에 담긴 RT 추출
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
