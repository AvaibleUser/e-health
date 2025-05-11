package org.ehealth.rx.domain.dto.report;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record ItemsSalePerEmployeeDto(
        //sales
        Long saleId,
        Integer quantity,
        BigDecimal unitPrice,
        Instant soldAt,
        //medicine
        BigDecimal unitCost,
        String name,

        //calculos
        BigDecimal Subtotal,
        BigDecimal SubCost,
        BigDecimal Profit
) {

}
