package org.ehealth.hr.domain.dto.reports;

import lombok.Builder;
import org.ehealth.hr.domain.dto.ContractDto;

import java.util.List;

@Builder(toBuilder = true)
public record HistoryEmployeeContractsDto(
        // data employee
        Long id,
        String fullName,
        String cui,
        String email,
        String areaName,
        // history contracts
        List<ContractDto> contracts
) {
}
