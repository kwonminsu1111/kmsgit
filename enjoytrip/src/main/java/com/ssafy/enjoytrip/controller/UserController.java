package com.ssafy.enjoytrip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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
import com.ssafy.enjoytrip.dto.response.UserResponse;
import com.ssafy.enjoytrip.model.User;
import com.ssafy.enjoytrip.service.UserService;
import com.ssafy.enjoytrip.util.ApiResponse;
import com.ssafy.enjoytrip.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Tag(name = "User 컨트롤러", description = "유저 회원가입, 로그인 및 마이페이지 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @Operation(summary = "회원가입", description = "새로운 유저 정보를 데이터베이스에 등록하고, 가입된 회원 정보를 반환합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody UserSignupRequest dto) {
    	userService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("회원가입 성공", "가입 완료"));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 검증하고, 세션에 로그인 정보를 저장합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @RequestBody UserLoginRequest dto, 
            HttpServletResponse response
    ) {
        User loginUser = userService.login(dto.getEmail(), dto.getPassword());
        
        if (loginUser != null) {
            // 토큰 생성 (인증은 이메일, 그러나 토큰 안에는 내부 식별자인 id를 담음)
            String token = jwtUtil.createToken(loginUser.getId());
            
            // HttpOnly 쿠키 봉투에 토큰 탑재
            Cookie cookie = new Cookie(JwtUtil.COOKIE_NAME, token);
            cookie.setHttpOnly(true);   // XSS 차단!
            cookie.setSecure(false);    // 로컬 http 테스트 환경이므로 false 설정
            cookie.setPath("/");        // 모든 백엔드 api 경로로 들어올 때 쿠키 지참 허용
            cookie.setMaxAge((int) (JwtUtil.EXPIRATION_TIME_MILLIS / 1000)); // 유효시간 1시간 설정
            
            response.addCookie(cookie); // Next.js 브라우저 서랍으로 슛!

            UserResponse userResponse = UserResponse.from(loginUser);
            return ResponseEntity.ok(ApiResponse.success("로그인이 성공적으로 완료되었습니다.", userResponse));
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    @Operation(summary = "로그아웃", description = "브라우저에 보관 중인 accessToken 쿠키를 즉시 만료시켜 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
    	// 똑같은 이름의 빈 쿠키를 만들고 수명을 0으로 줘서 브라우저에서 만료 유도
        Cookie cookie = new Cookie(JwtUtil.COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        
        response.addCookie(cookie);
        return ResponseEntity.ok(ApiResponse.success("로그아웃이 성공적으로 완료되었습니다.", "로그아웃 완료"));
    }

    @Operation(summary = "회원 탈퇴", description = "회원 데이터를 삭제하고 성공 여부(Boolean)를 반환합니다.")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            HttpServletRequest request, 
            HttpServletResponse response
    ) {
        // Interceptor가 쿠키 봉투를 열어 request 보관함에 안전하게 모셔둔 진짜 내 ID를 꺼냅니다.
        String userId = (String) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        
        // 진짜 내 ID를 들고 DB 영구 삭제
        userService.deleteUser(userId);
        
        // 탈퇴가 완료되었으니, 유저 브라우저 금고에 남아있던 accessToken 쿠키도 즉시 만료 시킵니다.
        Cookie cookie = new Cookie(JwtUtil.COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 수명 0초 부여로 즉시 사망 유도
        response.addCookie(cookie);
        
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 성공적으로 완료되었습니다.", "탈퇴 완료"));
    }
    
    // ====================================================================================================
    // [마이페이지]
    // ====================================================================================================

    @Operation(summary = "내 정보 조회 (마이페이지)", description = "내 상세 프로필을 안전하게 가져옵니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(HttpServletRequest request) {
        // 🕵️‍♂️ 인터셉터가 쿠키 뜯어서 request 보관함에 고이 모셔둔 진짜 내 ID 꺼내기!
        String userId = (String) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        
        User user = userService.getUserInfo(userId);
        UserResponse userResponse = UserResponse.from(user);
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회가 성공적으로 완료되었습니다.", userResponse));
    }

    @Operation(summary = "회원 정보 통합 수정", description = "닉네임, 비밀번호, 해시태그를 한 번에 수정합니다. 현재 비밀번호 검증이 필수입니다. (이메일 수정 불가)")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<String>> updateMyInfo(
            @RequestBody UserUpdateRequest updateRequest,
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        userService.updateUser(userId, updateRequest);
        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정이 성공적으로 완료되었습니다.", "수정 완료"));
    }
    
    // ==========================================
    // 마이페이지 전용 추가 API
    // ==========================================

    @Operation(summary = "내가 작성한 댓글 목록 조회", description = "마이페이지에서 로그인한 회원이 작성한 댓글과 해당 원본 게시글의 제목 목록을 조회합니다.")
    @GetMapping("/me/comments")
    public ResponseEntity<ApiResponse<List<UserCommentResponse>>> getUserComments(HttpServletRequest request) {
    	String userId = (String) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        List<UserCommentResponse> response = userService.getUserComments(userId);
        return ResponseEntity.ok(ApiResponse.success("내가 작성한 댓글 목록 조회가 성공적으로 완료되었습니다.", response));
    }

    @Operation(summary = "내가 좋아요 한 게시글 목록 조회", description = "마이페이지에서 로그인한 회원이 좋아요를 누른 게시글 목록을 조회합니다.")
    @GetMapping("/me/liked-boards")
    public ResponseEntity<ApiResponse<List<UserLikedBoardResponse>>> getUserLikedBoards(HttpServletRequest request) {
    	String userId = (String) request.getAttribute(JwtUtil.REQUEST_ATTRIBUTE_NAME);
        List<UserLikedBoardResponse> response = userService.getUserLikedBoards(userId);
        return ResponseEntity.ok(ApiResponse.success("내가 좋아요 한 게시글 목록 조회가 성공적으로 완료되었습니다.", response));
    }
}