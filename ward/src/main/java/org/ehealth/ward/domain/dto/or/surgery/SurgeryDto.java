package org.ehealth.ward.domain.dto.or.surgery;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import lombok.Builder;

@Builder(toBuilder = true)
public record SurgeryDto(
        Long id,
        LocalDate performedDate,
        String description,
        BigDecimal tariffHospitalCost,
        BigDecimal tariffSpecialistFee,
        BigDecimal tariffPrice,
        Instant createdAt,
        Instant updatedAt) {
}
