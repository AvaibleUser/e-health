package org.ehealth.hr.domain.dto.or;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record SurgeryPaymentDto (
        //tariff
        BigDecimal specialistFee,
        //Surgery
        Long id,
        String description,
        LocalDate performedDate,
        //SurgerySpecialist
        Long employeeId
){
}