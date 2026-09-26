package com.ecommerce.backend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard error payload returned to clients for every handled
 * exception. Kept structurally flat and consistent so clients can
 * parse all error responses the same way, regardless of which
 * exception triggered them.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;

    private int status;
    private String error;
    private String message;
    private String path;
}