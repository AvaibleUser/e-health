package org.ehealth.hr.domain.dto;

import lombok.Builder;
import org.ehealth.hr.domain.entity.AreaEntity;

import java.time.Instant;

@Builder(toBuilder = true)
public record AreaResponseDto(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
    public static AreaResponseDto fromEntity(AreaEntity entity) {
        return AreaResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
