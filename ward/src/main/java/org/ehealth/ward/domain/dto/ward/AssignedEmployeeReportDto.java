package org.ehealth.ward.domain.dto.ward;

import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record AssignedEmployeeReportDto(
        //assignedEmployee
        Long employeeId,
        //admission
        LocalDate admissionDate,
        LocalDate dischargeDate,
        //patients
        String fullName,
        String cui
) {
}
