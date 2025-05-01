package org.ehealth.rx.domain.dto.auth;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserDto(
        Long id,
        String email,
        boolean isDeleted,
        String roleName,
        Long cui,
        Instant createdAt,
        Instant updatedAt) {
}
