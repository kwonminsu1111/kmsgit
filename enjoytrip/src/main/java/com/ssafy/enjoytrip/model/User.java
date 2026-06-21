package com.ssafy.enjoytrip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String id;          // 사용자 아이디
    private String nickname;    // 닉네임
    private String email;       // 이메일
    private String password;    // 비밀번호 (해싱되어 저장될 예정)
    private String profilePath; // 프로필 URL
    private LocalDateTime regDate; // 가입시간
    private String role;        // 권한 (USER, ADMIN 등)
}