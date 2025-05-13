package org.ehealth.hr.client;

import org.ehealth.hr.domain.dto.or.SurgeryPaymentDto;
import org.ehealth.hr.domain.dto.reports.AssignedEmployeeReportDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@FeignClient(name = "ward", url = "${client.services.patient}/api/ward")
public interface PatientClient {

    @GetMapping(value = "/v1/surgeries/payments")
    List<SurgeryPaymentDto> getSurgeryPayments();

    @GetMapping(value = "/v1/surgeries/exist/{specialistId}")
    boolean existSurge(@PathVariable("specialistId") Long specialistId);

    @GetMapping(value = "/v1/assigned-employees/report/doctors")
    List<AssignedEmployeeReportDto> getDoctorsAssignedReport(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    );

}
