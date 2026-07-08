package com.ecommerce.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Generic response envelope wrapping every API response in a consistent
 * shape: { success, message, data }. Keeps success and error payloads
 * structurally identical so clients can rely on a single parsing path.
 *
 * Per spec, "data" is serialized explicitly as null when there is no
 * payload (e.g. "User registered successfully" with data: null) —
 * no @JsonInclude(NON_NULL) is applied, so the key always appears.
 */

@Schema(name = "Api Response", description = "Standard response envelope returned by every endpoint")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    @Schema(description = "Whether the request was processed successfully", example = "true")
    private boolean success;

    @Schema(description = "Human-readable result message", example = "Product created successfully")
    private String message;

    @Schema(description = "Response payload — null when no data is returned (e.g. delete operations)")
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder().success(true).message(message).data(data).build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder().success(true).message(message).data(null).build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder().success(false).message(message).data(null).build();
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder().success(false).message(message).data(data).build();
    }
}
