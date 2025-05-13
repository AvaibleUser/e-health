package org.ehealth.hr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.dto.or.PaymentPerSurgeryDto;
import org.ehealth.hr.domain.dto.reports.ReportExpensePayEmployeeDto;
import org.ehealth.hr.service.ISpecialistPaymentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class SpecialistPaymentController {
    private final ISpecialistPaymentService specialistPaymentService;

    @GetMapping
    public ResponseEntity<List<PaymentPerSurgeryDto>> getPaymentPerSurgery(){
        List<PaymentPerSurgeryDto> paymentPerSurgeryDtos = specialistPaymentService.getPaymentPerSurgery();
        return ResponseEntity.ok(paymentPerSurgeryDtos);
    }

    @PostMapping
    public ResponseEntity<Void> createPaymentPerSurgery(@RequestBody @Valid PaymentPerSurgeryDto surgeryPaymentDto){
        specialistPaymentService.createPaymentPerSurgery(surgeryPaymentDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/report/Expense")
    public ResponseEntity<ReportExpensePayEmployeeDto> getReportPayEmployeeInRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(this.specialistPaymentService.getReportPayEmployeeInRange(startDate, endDate));
    }

}
