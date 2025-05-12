package org.ehealth.rx.service;

import org.ehealth.rx.domain.dto.CreatePurchacheDto;
import org.ehealth.rx.domain.dto.report.MedicinePurchacheDto;
import org.ehealth.rx.domain.dto.report.ReportExpenseMedicinePurchacheDto;

import java.time.LocalDate;
import java.util.List;

public interface IPurchchesService {

    void create(Long medicineId, CreatePurchacheDto createPurchacheDto);
    ReportExpenseMedicinePurchacheDto getReportExpensePurchasesMedicine(List<MedicinePurchacheDto> items);
    ReportExpenseMedicinePurchacheDto getReportExpensePurchasesMedicineInRange(LocalDate startDate, LocalDate endDate);
}
