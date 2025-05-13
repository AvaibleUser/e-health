package org.ehealth.ward.domain.dto.finance.bill;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record BillDto(
        Long id,
        BigDecimal total,
        boolean isClosed,
        boolean isPaid,
        Instant createdAt,
        Instant updatedAt) {
}
