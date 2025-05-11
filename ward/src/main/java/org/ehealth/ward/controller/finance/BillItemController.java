package org.ehealth.ward.controller.finance;

import lombok.RequiredArgsConstructor;
import org.ehealth.ward.domain.dto.finance.report.ReportIncomeBill;
import org.ehealth.ward.service.finance.IBillItemService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/bill-items")
@RequiredArgsConstructor
public class BillItemController {
    private final IBillItemService billItemService;

    @GetMapping("/report/income")
    public ResponseEntity<ReportIncomeBill> getReportSalesTotalInRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(this.billItemService.getReportIncomeBillInRange(startDate, endDate));
    }

}
