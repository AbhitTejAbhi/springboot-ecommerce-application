package com.ecommerce.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyResult<T> {
    private boolean cached;
    private int httpStatus;
    private T body;
}
