package org.ehealth.ward.controller;

import lombok.RequiredArgsConstructor;
import org.ehealth.ward.domain.dto.or.SurgeryPaymentDto;
import org.ehealth.ward.service.ISurgeryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/surgeries")
@RequiredArgsConstructor
public class SurgeryController {
    private final ISurgeryService surgeryService;

    @GetMapping(value = "/payments")
    public List<SurgeryPaymentDto> getSurgeryPaymentDto(){
        return surgeryService.getSurgeryPaymentDto();
    }

    @GetMapping(value = "/exist/{specialistId}")
    public boolean existSurgeryPayment(@PathVariable Long specialistId) {
        return surgeryService.existSurgeryPayment(specialistId);
    }

}
