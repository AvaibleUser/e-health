package org.ehealth.ward.domain.dto.finance.billitem;

import java.math.BigDecimal;
import java.util.Optional;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddBillItemDto(
        @NotBlank String concept,
        BigDecimal amount,
        Optional<Long> saleId,
        Optional<Long> admissionId,
        Optional<Long> surgeryId) {
}
