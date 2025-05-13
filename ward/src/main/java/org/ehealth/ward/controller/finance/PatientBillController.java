package org.ehealth.ward.controller.finance;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;

import org.ehealth.ward.domain.dto.finance.bill.BillDto;
import org.ehealth.ward.domain.dto.finance.bill.UpdateBillDto;
import org.ehealth.ward.service.finance.IBillService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/patients/{patientId}/bills")
@RequiredArgsConstructor
public class PatientBillController {

    private final IBillService billService;

    @GetMapping
    public Page<BillDto> findPatientBills(@PathVariable long patientId, Pageable pageable) {
        return billService.findPatientBills(patientId, pageable);
    }

    @GetMapping("/opened")
    public List<BillDto> findOpenPatientBills(@PathVariable long patientId) {
        return billService.findOpenPatientBills(patientId);
    }

    @GetMapping("/{billId}")
    public ResponseEntity<BillDto> findPatientBillById(@PathVariable long patientId, @PathVariable long billId) {
        return ResponseEntity.of(billService.findPatientBillById(patientId, billId));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public void createPatientBill(@PathVariable long patientId) {
        billService.addBill(patientId);
    }

    @PutMapping("/{billId}")
    @ResponseStatus(NO_CONTENT)
    public void updatePatientBill(@PathVariable long billId, @PathVariable long patientId,
            @RequestBody @Valid UpdateBillDto bill) {
        billService.updateBill(billId, patientId, bill);
    }
}
