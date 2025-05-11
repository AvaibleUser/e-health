package org.ehealth.rx.domain.dto.report;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record ReportSaleMedicineDto(
        //medicine
        Long medicineId,
        String name,
        // totales
        Integer totalSold,
        BigDecimal totalIncome,
        BigDecimal totalProfit,
        List<ItemsSaleMedicineDto> items
) {
}
