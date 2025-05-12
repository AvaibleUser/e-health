package org.ehealth.ward.domain.dto.finance.report;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record ReportIncomeBill(
        BigDecimal totalIncome,
        List<BillItemReportDto> items
) {
}
