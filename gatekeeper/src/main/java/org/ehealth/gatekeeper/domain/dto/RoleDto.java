package org.ehealth.gatekeeper.domain.dto;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record RoleDto(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
