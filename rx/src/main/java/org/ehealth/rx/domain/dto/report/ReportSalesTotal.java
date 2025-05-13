package org.ehealth.rx.domain.dto.report;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record ReportSalesTotal(
        BigDecimal totalIncome,
        List<SaleMedicineDto> items
) {
}
