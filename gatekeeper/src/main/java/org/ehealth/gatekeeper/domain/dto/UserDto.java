package org.ehealth.gatekeeper.domain.dto;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserDto(
        Long id,
        String email,
        boolean isDeleted,
        String roleName,
        Long employeeCui,
        Instant createdAt,
        Instant updatedAt) {
}
