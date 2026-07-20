package com.sidney.banking.auth.api;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiError(
        int status,
        String message,
        Map<String, String> fields,
        OffsetDateTime timestamp
) {
}