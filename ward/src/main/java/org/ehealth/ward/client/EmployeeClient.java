package org.ehealth.ward.client;

import java.util.List;

import org.ehealth.ward.domain.dto.client.auth.EmployeeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hr", url = "${client.services.employee}/api/hr")
public interface EmployeeClient {

    @GetMapping("/v1/employees")
    List<EmployeeDto> findEmployeesByIds(@RequestParam List<Long> byIds);

    @GetMapping("/v1/employees/assignable")
    List<EmployeeDto> findAssignableEmployees();
}
