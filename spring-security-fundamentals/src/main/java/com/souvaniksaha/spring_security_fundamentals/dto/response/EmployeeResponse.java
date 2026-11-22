package com.souvaniksaha.spring_security_fundamentals.dto.response;

/*
 * Copyright (c) 2026 Souvanik Saha
 *
 * Licensed under the MIT License.
 * https://opensource.org/licenses/MIT
 */
public record EmployeeResponse(
        Long id,
        String name,
        String department
) {
}
