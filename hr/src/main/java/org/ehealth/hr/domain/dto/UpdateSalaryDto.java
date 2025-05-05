package org.ehealth.hr.domain.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record UpdateSalaryDto(
        @Positive
        BigDecimal salary,
        Boolean isIncrement
) {
}
