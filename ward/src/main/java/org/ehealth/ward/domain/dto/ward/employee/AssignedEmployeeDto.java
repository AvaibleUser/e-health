package org.ehealth.ward.domain.dto.ward.employee;

import java.time.Instant;

import org.ehealth.ward.domain.entity.ward.AssignedEmployeeEntity.AssignedEmployeeType;

import lombok.Builder;

@Builder(toBuilder = true)
public record AssignedEmployeeDto(
        Long id,
        Long employeeId,
        AssignedEmployeeType type,
        Instant createdAt,
        Instant updatedAt) {
}
