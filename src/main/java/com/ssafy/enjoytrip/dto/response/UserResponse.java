package com.ssafy.enjoytrip.dto.response;

import com.ssafy.enjoytrip.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 성공 및 회원 정보 조회 응답")
public class UserResponse {

    @Schema(description = "사용자 아이디", example = "ssafy123")
    private String id;

    @Schema(description = "닉네임", example = "싸피마스터")
    private String nickname;

    @Schema(description = "이메일", example = "ssafy@ssafy.com")
    private String email;

    @Schema(description = "프로필 이미지 경로(Base64)", example = "data:image/png;base64,iVBORw0KGgoAAAANS...")
    private String profilePath;

    @Schema(description = "권한", example = "USER")
    private String role;

    // User 엔티티를 받아서 바로 응답 DTO로 변환해주는 정적 팩토리 메서드
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getProfilePath(),
                user.getRole()
        );
    }
}