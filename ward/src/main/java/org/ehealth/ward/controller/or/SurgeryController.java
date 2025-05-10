package org.ehealth.ward.controller.or;

import java.util.List;

import org.ehealth.ward.domain.dto.or.SurgeryPaymentDto;
import org.ehealth.ward.service.or.ISurgeryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/surgeries")
@RequiredArgsConstructor
public class SurgeryController {
    private final ISurgeryService surgeryService;

    @GetMapping(value = "/payments")
    public List<SurgeryPaymentDto> getSurgeryPaymentDto() {
        return surgeryService.getSurgeryPaymentDto();
    }

    @GetMapping(value = "/exist/{specialistId}")
    public boolean existSurgeryPayment(@PathVariable Long specialistId) {
        return surgeryService.existSurgeryPayment(specialistId);
    }

}
