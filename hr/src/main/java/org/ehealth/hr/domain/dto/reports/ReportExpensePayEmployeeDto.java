package org.ehealth.hr.domain.dto.reports;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record ReportExpensePayEmployeeDto (
        BigDecimal totalAmount,
        List<PaymentEmployeeDto> items
) {
}
