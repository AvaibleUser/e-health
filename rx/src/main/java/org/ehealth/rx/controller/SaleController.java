package org.ehealth.rx.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ehealth.rx.domain.dto.CreateSaleDto;
import org.ehealth.rx.domain.dto.report.ReportSaleMedicineDto;
import org.ehealth.rx.domain.dto.report.ReportSalesPerEmployeeDto;
import org.ehealth.rx.service.ISaleService;
import org.ehealth.rx.util.annotation.CurrentUserCui;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final ISaleService saleService;

    @PostMapping()
    public ResponseEntity<Void> createSaleTotal(
            @CurrentUserCui String cui,
            @Valid @RequestBody CreateSaleDto createSaleDto
    ) {
        saleService.createSaleTotal(cui, createSaleDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/report/medicine")
    public ResponseEntity<List<ReportSaleMedicineDto>> getReportSalesMedicinePerMedicineInRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(this.saleService.getReportSalesMedicinePerMedicineInRange(startDate, endDate));
    }

    @GetMapping("/report/employees")
    public ResponseEntity<List<ReportSalesPerEmployeeDto>> getReportSalesMedicineEmployeeInRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(this.saleService.getReportSalesMedicineEmployeeInRange(startDate, endDate));
    }
}
