package org.ehealth.hr.domain.dto.vacation;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record CreateRequestVacationDto(
        Long employeeId,
        LocalDate startDate,
        @Positive(message = "Los dias deben ser mayor a 0")
        Integer days
) {
}
