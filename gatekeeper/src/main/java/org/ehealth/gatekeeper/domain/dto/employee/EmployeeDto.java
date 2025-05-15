package org.ehealth.gatekeeper.domain.dto.employee;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder(toBuilder = true)
public record EmployeeDto(
        @JsonProperty("employeeId") Long id,
        String fullName,
        String cui,
        String phone,
        String email,
        boolean isSpecialist,
        String areaName,
        Instant createdAt,
        Instant updatedAt) {
}
