package org.ehealth.hr.domain.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record NewContractDto(
        @Positive Long idContract,
        @Positive Long idEmployee,
        @Positive BigDecimal salary,
        @PositiveOrZero BigDecimal igssDiscount,
        @PositiveOrZero BigDecimal irtraDiscount
) {
}
