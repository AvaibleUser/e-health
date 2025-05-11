package org.ehealth.rx.service;

import org.ehealth.rx.domain.dto.CreateSaleDto;
import org.ehealth.rx.domain.dto.ItemSaleDto;
import org.ehealth.rx.domain.dto.employee.EmployeeDto;
import org.ehealth.rx.domain.dto.report.ReportSaleMedicineDto;
import org.ehealth.rx.domain.dto.report.SaleMedicineDto;
import org.ehealth.rx.domain.entity.MedicineEntity;
import org.ehealth.rx.domain.entity.SaleEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface ISaleService {

    void createSaleTotal(String cui, CreateSaleDto createSaleDto);
    EmployeeDto validateEntities(String cui, Long patientId);
    void validateItemList(List<ItemSaleDto> items);
    Set<Long> extractMedicineIds(List<ItemSaleDto> items);
    Map<Long, MedicineEntity> loadMedicinesById(Set<Long> medicineIds);
    void validateStockAvailability(Map<Long, MedicineEntity> medicineMap, List<ItemSaleDto> items);
    List<SaleEntity> buildSales(Long employeeId, CreateSaleDto dto, Map<Long, MedicineEntity> medicineMap);
    List<ReportSaleMedicineDto> getReportSalesMedicinePerMedicineInRange(LocalDate starDate, LocalDate endDate);
    List<ReportSaleMedicineDto> getReportSalesMedicinePerMedicine(List<SaleMedicineDto> saleMedicineDtos);
}
