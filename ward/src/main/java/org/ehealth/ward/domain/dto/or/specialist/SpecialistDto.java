package org.ehealth.ward.domain.dto.or.specialist;

import java.time.Instant;

import org.ehealth.ward.domain.entity.or.SurgerySpecialistEntity.SurgerySpecialistType;

import lombok.Builder;

@Builder(toBuilder = true)
public record SpecialistDto(
        Long id,
        Long employeeId,
        SurgerySpecialistType type,
        Instant createdAt,
        Instant updatedAt) {
}
