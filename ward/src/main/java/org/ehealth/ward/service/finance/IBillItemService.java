package org.ehealth.ward.service.finance;

import org.ehealth.ward.domain.dto.finance.report.BillItemReportDto;
import org.ehealth.ward.domain.dto.finance.report.ReportIncomeBill;

import java.time.LocalDate;
import java.util.List;

public interface IBillItemService {
    ReportIncomeBill getReportIncomeBill(List<BillItemReportDto> items);
    ReportIncomeBill getReportIncomeBillInRange(LocalDate startDate, LocalDate endDate);
}
