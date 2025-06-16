package com.PBO.TaleSwipe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String code;
    private String message;
    private T data;
    private ErrorResponse error;
    private PaginationResponse pagination;

    public static <T> ApiResponse<T> success(int status, String code, String message, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String code, String message, ErrorResponse error) {
        return ApiResponse.<T>builder()
                .status(status)
                .code(code)
                .message(message)
                .error(error)
                .build();
    }

    public static <T> ApiResponse<T> successWithPagination(int status, String code, String message, T data, PaginationResponse pagination) {
        return ApiResponse.<T>builder()
                .status(status)
                .code(code)
                .message(message)
                .data(data)
                .pagination(pagination)
                .build();
    }
} 
