package org.ehealth.hr.domain.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record NewContractDto(
        @Positive Long idContract,
        @Positive Long idEmployee,
        @Positive BigDecimal salary,
        @Positive BigDecimal igssDiscount,
        @Positive BigDecimal irtraDiscount
) {
}
