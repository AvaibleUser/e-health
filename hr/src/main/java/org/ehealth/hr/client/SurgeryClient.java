package org.ehealth.hr.client;

import org.ehealth.hr.domain.dto.or.SurgeryPaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

@FeignClient(name = "ward", url = "${client.services.patient}/api/ward")
public interface SurgeryClient {

    @GetMapping(value = "/v1/surgeries/payments")
    List<SurgeryPaymentDto> getSurgeryPayments();

    @GetMapping(value = "/exist/{specialistId}")
    boolean existSurge(@PathVariable("specialistId") Long specialistId);

}
