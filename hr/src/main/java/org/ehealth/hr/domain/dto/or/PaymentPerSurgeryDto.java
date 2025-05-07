package org.ehealth.hr.domain.dto.or;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record PaymentPerSurgeryDto(
        //tariff
        BigDecimal specialistFee,

        //Surgery
        Long id,
        String description,
        LocalDate performedDate,

        //employee
        Long employeeId,
        String fullName,
        String Cui
) {
}
