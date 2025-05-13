package org.ehealth.hr.domain.dto.or;

import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record SpecialistPaymentDto(
        Long id,
        Long surgeryId,
        Long employeeId
) {
}
