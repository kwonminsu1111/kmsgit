package com.ssafy.enjoytrip.dto.request;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "통합 회원 정보 수정 요청 데이터")
public class UserUpdateRequest {

    @Schema(description = "변경할 닉네임", example = "싸피킴")
    private String nickname;

    @Schema(description = "현재 비밀번호 (본인 확인 및 검증용 필수 데이터)", example = "current123!")
    private String currentPassword;

    @Schema(description = "새로운 비밀번호 (변경할 경우에만 입력, 미변경 시 null 혹은 빈 문자열)", example = "newPass123!")
    private String newPassword;

    @Schema(description = "Base64 프로필 이미지 데이터 (미변경 시 null)", example = "data:image/png;base64,...")
    private String profilePath;

    @Schema(description = "선택한 해시태그 ID 리스트", example = "[1, 2, 3]")
    private List<Integer> hashtags;
}