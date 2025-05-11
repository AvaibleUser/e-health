package org.ehealth.rx.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record CreateMedicineDto(
        @NotBlank String name,
        @Positive BigDecimal unitPrice,
        @Positive BigDecimal unitCost,
        @PositiveOrZero int stock,
        @PositiveOrZero int minStock
) {
}
