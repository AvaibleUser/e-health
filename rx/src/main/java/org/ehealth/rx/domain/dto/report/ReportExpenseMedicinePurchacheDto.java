package org.ehealth.rx.domain.dto.report;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ReportExpenseMedicinePurchacheDto(
        BigDecimal amountExpense,
        List<MedicinePurchacheDto> items

) {
}
