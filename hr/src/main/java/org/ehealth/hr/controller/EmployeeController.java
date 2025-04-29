package org.ehealth.hr.controller;


import lombok.RequiredArgsConstructor;
import org.ehealth.hr.service.EmployeeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;


}
