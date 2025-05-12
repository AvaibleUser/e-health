package org.ehealth.rx.domain.dto.report;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record MedicinePurchacheDto(
        //medicine
        Long medicineId,
        String name,
        //purchase
        Long purchaseId,
        Integer quantity,
        BigDecimal unitCost,
        Instant purchasedAt
) {
}
