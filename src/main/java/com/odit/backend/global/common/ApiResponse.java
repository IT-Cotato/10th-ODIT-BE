package com.odit.backend.global.common;

import com.odit.backend.global.error.ErrorResponse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 통일된 API 응답 형식을 정의한 클래스
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {
    private boolean success;  // 요청 성공 여부
    private T data;           // 응답 데이터 (성공 시)
    private ErrorResponse error; // 에러 정보 (실패 시)

    /**
     * 성공 응답 생성 메서드
     *
     * @param data 성공 데이터
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    /**
     * 실패 응답 생성 메서드
     *
     * @param error 에러 정보
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> failure(ErrorResponse error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = error;
        return response;
    }
}
