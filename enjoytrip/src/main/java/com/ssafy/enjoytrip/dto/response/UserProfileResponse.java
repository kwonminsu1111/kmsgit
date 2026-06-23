package com.ssafy.enjoytrip.dto.response;

import java.util.List;

import com.ssafy.enjoytrip.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "User profile response")
public class UserProfileResponse {

    @Schema(description = "nickname", example = "ssafy123")
    private String nickname;

    @Schema(description = "email", example = "abc@ssafy.com")
    private String email;

    @Schema(description = "profile image path or Base64 data", example = "data:image/png;base64,...")
    private String profile_path;

    @Schema(description = "User hashtag names", example = "[\"맛집투어\", \"힐링\"]")
    private List<String> hashtags;

    public static UserProfileResponse from(User user, List<String> hashtags) {
        return new UserProfileResponse(
                user.getNickname(),
                user.getEmail(),
                user.getProfilePath(),
                hashtags
        );
    }
}
