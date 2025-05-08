package org.ehealth.ward.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.ehealth.ward.domain.dto.ward.AssignedEmployeeReportDto;
import org.ehealth.ward.service.IAssignedEmployeeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/assigned-employees")
@RequiredArgsConstructor
public class AssignedEmployeeController {

    private final IAssignedEmployeeService assignedEmployeeService;

    @PermitAll
    @GetMapping("/report/doctors")
    public List<AssignedEmployeeReportDto> getDoctorsAssignedReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return assignedEmployeeService.getAssignedDoctorsReport(startDate, endDate);
    }
}

