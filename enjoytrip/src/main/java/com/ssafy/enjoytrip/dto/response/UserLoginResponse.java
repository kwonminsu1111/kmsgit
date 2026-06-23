package com.ssafy.enjoytrip.dto.response;

import com.ssafy.enjoytrip.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Login response")
public class UserLoginResponse {

    @Schema(description = "Email", example = "abc@ssafy.com")
    private String email;

    @Schema(description = "Nickname", example = "ssafy123")
    private String nickname;

    @Schema(description = "Profile image path or Base64 data", example = "data:image/png;base64,...")
    private String profile_path;

    @Schema(description = "JWT access token")
    private String accessToken;

    public static UserLoginResponse from(User user, String accessToken) {
        return new UserLoginResponse(
                user.getEmail(),
                user.getNickname(),
                user.getProfilePath(),
                accessToken
        );
    }
}
