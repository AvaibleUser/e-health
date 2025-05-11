package org.ehealth.rx.domain.dto.employee;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record EmployeeDto(
        Long id,
        String fullName,
        String cui,
        String phone,
        String email,
        boolean isSpecialist,
        String areaName,
        Instant createdAt,
        Instant updatedAt) {
}
