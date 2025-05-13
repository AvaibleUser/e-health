package org.ehealth.ward.domain.dto.finance.tariff;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record TariffDto(
        Long id,
        String description,
        BigDecimal hospitalCost,
        BigDecimal specialistFee,
        BigDecimal price,
        Instant createdAt,
        Instant updatedAt) {
}
