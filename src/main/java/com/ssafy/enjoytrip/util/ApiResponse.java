package com.ssafy.enjoytrip.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Common REST API response")
public class ApiResponse<T> {

    @Schema(description = "Request success status", example = "true")
    private boolean isSuccess;

    @Schema(description = "Response code", example = "200")
    private String code;

    @Schema(description = "Response message", example = "Request succeeded.")
    private String message;

    @Schema(description = "Response data")
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "200", "요청에 성공했습니다.", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, "200", message, data);
    }

    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return new ApiResponse<>(true, code, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, "400", message, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}