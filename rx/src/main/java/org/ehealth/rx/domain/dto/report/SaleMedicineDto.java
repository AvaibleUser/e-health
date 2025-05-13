package org.ehealth.rx.domain.dto.report;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record SaleMedicineDto(
        //medicine
        Long medicineId,
        String name,
        BigDecimal unitCost,
        //sale
        Long saleId,
        Integer quantity,
        BigDecimal unitPrice,
        Instant soldAt,
        //employee
        Long employeeId
) {
}
