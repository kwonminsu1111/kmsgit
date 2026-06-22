package com.ssafy.enjoytrip.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "공통 REST API 응답 규격")
public class ApiResponse<T> {
	
    @Schema(description = "요청 처리 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지 (안내 및 에러 문구)", example = "요청에 성공했습니다.")
    private String message;

    @Schema(description = "실제 반환될 데이터 (DTO, List 등 다양한 객체 진입)")
    private T data;

    // 성공 시 데이터를 담아 빠르게 객체를 생성하는 static 메서드 (정적 팩토리 메서드)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청에 성공했습니다.", data);
    }

    // 성공 시 커스텀 메시지와 데이터를 함께 담아 보내는 메서드
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // 실패(에러) 시 에러 메시지를 담아 보내는 메서드 (데이터는 null 처리)
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}