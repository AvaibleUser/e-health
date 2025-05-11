package org.ehealth.rx.domain.dto.report;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record ReportSalesPerEmployeeDto(
        //employee
        Long employeeId,
        String employeeName,
        String cui,
        // totales
        Integer totalSold,
        BigDecimal totalIncome,
        BigDecimal totalProfit,
        List<ItemsSalePerEmployeeDto> items
) {
}
