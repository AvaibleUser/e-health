package org.ehealth.ward.controller.ward;

import java.util.List;

import org.ehealth.ward.domain.dto.ward.employee.CompleteEmployeeDto;
import org.ehealth.ward.service.ward.IAssignedEmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/patients/{patientId}/admissions/{admissionId}/assigned-employees")
@RequiredArgsConstructor
public class AdmissionEmployeesController {

    private final IAssignedEmployeeService assignedEmployeeService;

    @GetMapping
    public List<CompleteEmployeeDto> getAssignedEmployees(@PathVariable long patientId,
            @PathVariable long admissionId) {
        return assignedEmployeeService.getAssignedEmployees(patientId, admissionId);
    }

    @PutMapping
    public void assignEmployees(@PathVariable long patientId, @PathVariable long admissionId,
            @RequestBody List<Long> employeeIds) {
        assignedEmployeeService.assignEmployees(patientId, admissionId, employeeIds);
    }
}
