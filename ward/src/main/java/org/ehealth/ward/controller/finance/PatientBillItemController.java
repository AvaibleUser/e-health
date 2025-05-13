package org.ehealth.ward.controller.finance;

import static org.springframework.http.HttpStatus.CREATED;

import org.ehealth.ward.domain.dto.finance.billitem.AddBillItemDto;
import org.ehealth.ward.domain.dto.finance.billitem.BillItemDto;
import org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType;
import org.ehealth.ward.service.finance.IBillItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/patients/{patientId}/bills")
@RequiredArgsConstructor
public class PatientBillItemController {

    private final IBillItemService billItemService;

    @GetMapping("/items")
    public Page<BillItemDto> findAllBillItems(@PathVariable long patientId,
            @RequestParam(required = false) BillItemType type, Pageable pageable) {
        return billItemService.findPatientBillItems(patientId, pageable, type);
    }

    @GetMapping("/{billId}/items")
    public Page<BillItemDto> findPatientBillItems(@PathVariable long patientId, @PathVariable long billId,
            @RequestParam(required = false) BillItemType type, Pageable pageable) {
        return billItemService.findPatientBillItems(patientId, billId, pageable, type);
    }

    @PostMapping("/consultations")
    @ResponseStatus(CREATED)
    public void addConsultation(@PathVariable long patientId, @RequestBody @Valid AddBillItemDto billItem) {
        billItemService.addConsultation(patientId, billItem);
    }

    @PostMapping("/{billId}/items")
    @ResponseStatus(CREATED)
    public void addBillItem(@PathVariable long patientId, @PathVariable long billId,
            @RequestBody @Valid AddBillItemDto billItem) {
        billItemService.addBillItem(patientId, billId, billItem);
    }
}
