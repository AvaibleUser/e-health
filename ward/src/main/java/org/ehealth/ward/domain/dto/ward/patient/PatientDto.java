package org.ehealth.ward.domain.dto.ward.patient;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Builder;

@Builder(toBuilder = true)
public record PatientDto(
        Long id,
        String fullName,
        String cui,
        LocalDate birthDate,
        String phone,
        String email,
        Instant createdAt,
        Instant updatedAt) {
}
