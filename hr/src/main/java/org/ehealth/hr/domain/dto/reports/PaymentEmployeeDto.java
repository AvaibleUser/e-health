package org.ehealth.hr.domain.dto.reports;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record PaymentEmployeeDto(
        //employee
        Long employeeId,
        String fullName,
        String cui,
        //payment
        BigDecimal amount,
        Instant paidAt
) {
}
