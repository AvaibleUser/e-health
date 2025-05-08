package org.ehealth.hr.domain.dto.reports;

import lombok.Builder;
import org.ehealth.hr.domain.dto.ContractDto;

import java.util.List;

@Builder(toBuilder = true)
public record HistoryAssignedEmployee(
        //employee
        Long id,
        String fullName,
        String cui,
        String email,
        //contract
        ContractDto contract,
        // assignaciones
        List<AssignedDto> assignedList
) {
}
