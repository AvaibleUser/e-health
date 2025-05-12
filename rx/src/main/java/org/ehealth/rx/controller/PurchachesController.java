package org.ehealth.rx.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ehealth.rx.domain.dto.CreatePurchacheDto;
import org.ehealth.rx.domain.dto.report.ReportExpenseMedicinePurchacheDto;
import org.ehealth.rx.service.IPurchchesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/purchaches")
@RequiredArgsConstructor
public class PurchachesController {

    private final IPurchchesService purchesService;

    @PostMapping("/{medicineId}")
    public ResponseEntity<Void> create(@Valid @RequestBody CreatePurchacheDto dto, @PathVariable Long medicineId) {
        purchesService.create(medicineId,dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/report/Expense")
    public ResponseEntity<ReportExpenseMedicinePurchacheDto> getReportExpensePurchasesMedicineInRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(this.purchesService.getReportExpensePurchasesMedicineInRange(startDate, endDate));
    }
}
