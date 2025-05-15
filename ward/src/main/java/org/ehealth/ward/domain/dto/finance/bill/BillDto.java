package org.ehealth.ward.domain.dto.finance.bill;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

    public BillDto(NativeBillDto nativeBillDto) {
        this(nativeBillDto.id().longValue(),
                BigDecimal.valueOf(nativeBillDto.total().doubleValue()),
                nativeBillDto.isClosed(),
                nativeBillDto.isPaid(),
                nativeBillDto.createdAt().toInstant(),
                nativeBillDto.updatedAt().toInstant());
    }

    public static record NativeBillDto(
            Number id,
            Number total,
            boolean isClosed,
            boolean isPaid,
            Timestamp createdAt,
            Timestamp updatedAt) {
    }
}
