package org.ehealth.gatekeeper.client;

import org.ehealth.gatekeeper.domain.dto.employee.EmployeeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "hr", url = "${client.services.employee}/api/hr")
public interface EmployeeClient {

    @GetMapping(value = "/v1/employees/cui/{cui}")
    EmployeeDto findEmployeeByCui(@PathVariable String cui);

    @GetMapping(value = "/v1/employees/all")
    List<EmployeeDto> findAllEmployees();
}
