package org.ehealth.hr.controller;

import org.ehealth.hr.domain.dto.EmployeeDto;
import org.ehealth.hr.service.IEmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final IEmployeeService employeeService;

    @PermitAll
    @GetMapping(value = "/{cui}", params = "by=cui")
    public EmployeeDto findEmployeeByCui(@PathVariable String cui) {
        return employeeService.findEmployeeByCui(cui);
    }
}
