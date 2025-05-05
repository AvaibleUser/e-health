package org.ehealth.hr.domain;

import lombok.Builder;
import org.ehealth.hr.domain.entity.ContractEntity;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record FinishContract(
        Long idContract,
        ContractEntity.TerminationReason terminationReason,
        String description,
        LocalDate date
) {
}
