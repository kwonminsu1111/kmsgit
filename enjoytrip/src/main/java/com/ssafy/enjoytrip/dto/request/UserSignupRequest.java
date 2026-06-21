package com.ssafy.enjoytrip.dto.request;

import com.ssafy.enjoytrip.model.User;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class UserSignupRequest {
    private String id;
    private String nickname;
    private String email;
    private String password;
    private String profilePath;

    // DTO 데이터를 바탕으로 순수한 유저 엔티티를 찍어내는 메서드
    public User toEntity() {
        return User.builder()
                .id(this.id)
                .nickname(this.nickname)
                .email(this.email)
                .password(this.password) // 실제로는 암호화 필요
                .profilePath(this.profilePath)
                .regDate(LocalDateTime.now())
                .role("USER") // 회원가입 시 기본 권한 주입
                .build();
    }
}