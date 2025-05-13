package org.ehealth.ward.service.finance;

import java.time.LocalDate;
import java.util.List;

import org.ehealth.ward.domain.dto.finance.billitem.AddBillItemDto;
import org.ehealth.ward.domain.dto.finance.billitem.BillItemDto;
import org.ehealth.ward.domain.dto.finance.report.BillItemReportDto;
import org.ehealth.ward.domain.dto.finance.report.ReportIncomeBill;
import org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBillItemService {

    Page<BillItemDto> findPatientBillItems(long patientId, long billId, Pageable pageable, BillItemType type);

    Page<BillItemDto> findPatientBillItems(long patientId, Pageable pageable, BillItemType type);

    void addConsultation(long patientId, AddBillItemDto billItem);

    void addBillItem(long patientId, long billId, AddBillItemDto billItem);

    ReportIncomeBill getReportIncomeBill(List<BillItemReportDto> items);

    ReportIncomeBill getReportIncomeBillInRange(LocalDate startDate, LocalDate endDate);
}
