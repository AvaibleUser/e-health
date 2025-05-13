package org.ehealth.rx.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ward", url = "${client.services.patient}/api/ward")
public interface PatientClient {


    @GetMapping(value = "/v1/patients/exist/{id}")
    boolean existSurge(@PathVariable Long id);


}
