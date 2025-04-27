package org.ehealth.gatekeeper.domain.dto;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserDto(
        Long id,
        String username,
        String email,
        boolean isDeleted,
        Instant createdAt,
        Instant updatedAt) {
}
