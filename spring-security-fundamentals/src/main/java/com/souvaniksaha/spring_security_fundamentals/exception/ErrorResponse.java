package com.souvaniksaha.spring_security_fundamentals.exception;

/*
 * Copyright (c) 2026 Souvanik Saha
 *
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */

import java.time.Instant;

public record ErrorResponse(
        boolean success,
        String message,
        String path,
        Instant timestamp
) {
    public static ErrorResponse of(String message, String path) {
        return new ErrorResponse(
                false,
                message,
                path,
                Instant.now()
        );
    }
}