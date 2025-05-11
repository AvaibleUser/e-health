package org.ehealth.ward.domain.dto.client.employee;

import java.time.Instant;

import lombok.Builder;

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
