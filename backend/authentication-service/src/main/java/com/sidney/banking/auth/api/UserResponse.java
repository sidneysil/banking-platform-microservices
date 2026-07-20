package com.sidney.banking.auth.api;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.sidney.banking.auth.user.User;
import com.sidney.banking.auth.user.UserRole;
import com.sidney.banking.auth.user.UserStatus;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        UserRole role,
        UserStatus status,
        OffsetDateTime createdAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
