package org.ehealth.ward.controller.ward;

import java.time.LocalDate;
import java.util.List;

import org.ehealth.ward.domain.dto.ward.employee.AssignedEmployeeReportDto;
import org.ehealth.ward.domain.dto.ward.employee.CompleteEmployeeDto;
import org.ehealth.ward.service.ward.IAssignedEmployeeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/assigned-employees")
@RequiredArgsConstructor
public class AssignedEmployeeController {

    private final IAssignedEmployeeService assignedEmployeeService;

    @GetMapping("/report/doctors")
    public List<AssignedEmployeeReportDto> getDoctorsAssignedReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return assignedEmployeeService.getAssignedDoctorsReport(startDate, endDate);
    }

    @GetMapping("/assignable")
    public List<CompleteEmployeeDto> getAssignableEmployees() {
        return assignedEmployeeService.getAssignableEmployees();
    }
}
