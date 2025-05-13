package org.ehealth.gatekeeper.domain.dto.employee;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record UserEmployeeDto(
        //User
        Long id,
        boolean active,
        String roleName,
        Instant createdAt,
        Instant updatedAt,
        //employee
        Long employeeId,
        String fullName,
        String cui,
        String phone,
        String email,
        boolean isSpecialist,
        String areaName
) {
}
