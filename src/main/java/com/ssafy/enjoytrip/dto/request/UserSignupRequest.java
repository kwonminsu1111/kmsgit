package com.ssafy.enjoytrip.dto.request;

import java.time.LocalDateTime;

import com.ssafy.enjoytrip.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest {
    private String nickname;
    private String email;
    private String password;

    public User toEntity(String encryptedPassword) {
        return User.builder()
                .nickname(this.nickname)
                .email(this.email)
                .password(encryptedPassword)
                .profilePath("")
                .regDate(LocalDateTime.now())
                .role("USER")
                .build();
    }
}
