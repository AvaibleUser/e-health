package org.ehealth.hr.domain.dto.vacation;

import lombok.Builder;
import org.ehealth.hr.domain.entity.VacationEntity;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record VacationPendingDto(
        //employee
        Long employeeId,
        String fullName,
        String cui,
        //area work
        String name,
        //vacation
        Long id,
        LocalDate requestedDate,
        LocalDate startDate,
        LocalDate endDate,
        boolean approved,
        VacationEntity.State state
) {
}
