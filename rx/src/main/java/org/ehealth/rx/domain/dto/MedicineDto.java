package org.ehealth.rx.domain.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record MedicineDto(
        Long id,
        String name,
        BigDecimal unitPrice,
        BigDecimal unitCost,
        int stock,
        int minStock,
        Instant createdAt,
        Instant updatedAt
) {
}
