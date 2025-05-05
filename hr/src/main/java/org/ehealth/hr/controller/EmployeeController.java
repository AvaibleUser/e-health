package org.ehealth.hr.controller;

import org.ehealth.hr.domain.dto.CreateEmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeResponseDto;
import org.ehealth.hr.service.IEmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final IEmployeeService employeeService;

    @PermitAll
    @GetMapping(value = "/cui/{cui}")
    public EmployeeDto findEmployeeByCui(@PathVariable String cui) {
        return employeeService.findEmployeeByCui(cui);
    }

    @PostMapping()
    public ResponseEntity<EmployeeResponseDto> createEmployee(@RequestBody CreateEmployeeDto dto) {
        EmployeeResponseDto response = employeeService.createEmployee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> findAllEmployees() {
        List<EmployeeDto> employees = employeeService.findAllEmployeesOrdered();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/area/{areaId}")
    public ResponseEntity<List<EmployeeDto>> findEmployeesByArea(@PathVariable Long areaId) {
        List<EmployeeDto> employees = employeeService.findEmployeesByArea(areaId);
        return ResponseEntity.ok(employees);
    }

}
