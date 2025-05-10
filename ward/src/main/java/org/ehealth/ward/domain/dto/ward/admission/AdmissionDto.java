package org.ehealth.ward.domain.dto.ward.admission;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.ehealth.ward.domain.entity.ward.AdmissionEntity.AdmissionStatus;

import lombok.Builder;

@Builder(toBuilder = true)
public record AdmissionDto(
        Long id,
        LocalDate admissionDate,
        LocalDate dischargeDate,
        AdmissionStatus status,
        Long roomId,
        String roomNumber,
        BigDecimal roomCostPerDay,
        Instant createdAt,
        Instant updatedAt) {
}