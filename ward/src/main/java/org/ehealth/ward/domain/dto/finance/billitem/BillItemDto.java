package org.ehealth.ward.domain.dto.finance.billitem;

import java.math.BigDecimal;
import java.time.Instant;

import org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType;

import lombok.Builder;

@Builder(toBuilder = true)
public record BillItemDto(
        Long id,
        String concept,
        BigDecimal amount,
        BillItemType type,
        Instant createdAt,
        Instant updatedAt) {
}
