package org.ehealth.hr.domain.dto.reports;

import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record AssignedDto(
        LocalDate admissionDate,
        LocalDate dischargeDate,
        //patients
        String fullName,
        String cui
) {
}
