package org.ehealth.hr.domain.dto;

import lombok.Builder;
import org.ehealth.hr.domain.entity.EmployeeEntity;

import java.time.Instant;

@Builder(toBuilder = true)
public record EmployeeResponseDto(
        Long id,
        String fullName,
        String cui,
        String phone,
        String email,
        boolean isSpecialist,
        Instant createdAt
) {
    public static EmployeeResponseDto fromEntity(EmployeeEntity entity) {
        return EmployeeResponseDto
                .builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .cui(entity.getCui())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .isSpecialist(entity.isSpecialist())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
