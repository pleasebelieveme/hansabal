package org.example.hansabal.domain.review.apiresponse;

import java.time.LocalDateTime;

public class ApiResponseDto<T> {

    private final LocalDateTime timestamp;
    private final String message;
    private final T data;

    public ApiResponseDto(SuccessCode successCode, T data) {
        this.timestamp = LocalDateTime.now();
        this.message = successCode.getMessage();
        this.data = data;
    }

    public static <T> ApiResponseDto<T> success(SuccessCode successCode,T data) {
        return new ApiResponseDto<>(successCode, data);
    }
}
