package org.ehealth.ward.domain.dto.finance.report;

import lombok.Builder;
import org.ehealth.ward.domain.entity.finance.BillItemEntity;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record BillItemReportDto(
        Long id,
        String concept,
        BigDecimal amount,
        BillItemEntity.BillItemType type,
        Instant createdAt
) {
}
