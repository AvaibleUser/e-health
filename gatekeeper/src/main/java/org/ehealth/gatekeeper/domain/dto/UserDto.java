package org.ehealth.gatekeeper.domain.dto;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserDto(
        Long id,
        String email,
        String cui,
        boolean active,
        String roleName,
        Instant createdAt,
        Instant updatedAt) {
}
