package org.ehealth.hr.controller;

import org.ehealth.hr.domain.dto.CreateEmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeResponseDto;
import org.ehealth.hr.domain.dto.reports.ReportAssignedEmployeeDto;
import org.ehealth.hr.service.IEmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
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

    @PermitAll
    @GetMapping(value = "/all")
    public List<EmployeeDto> findAllEmployee() {
        return employeeService.findAllEmployeesOrdered();
    }

    @GetMapping(value = "/exist/{id}")
    public boolean existEmployeeById(@PathVariable Long id) {
        return employeeService.existEmployeeById(id);
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

    /**
     * Endpoint para obtener el historial de asignaciones de empleados a admisiones
     *
     * @param filter    (opcional) Filtro de asignaciones:
     *                  1 = solo vigentes,
     *                  2 = solo finalizadas,
     *                  otro = todas
     * @param startDate (requerido) Fecha de inicio del rango
     * @param endDate   (requerido) Fecha de fin del rango
     */
    @GetMapping("/assigned/report/doctors/{filter}")
    public ResponseEntity<ReportAssignedEmployeeDto> getAssignedReport(
            @PathVariable Integer filter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        ReportAssignedEmployeeDto report = employeeService.getReportAssignedEmployeeInRange(filter, startDate, endDate);
        return ResponseEntity.ok(report);
    }



}
