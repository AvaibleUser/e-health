package org.ehealth.hr.domain.dto;

import lombok.Builder;
import org.ehealth.hr.domain.entity.ContractEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record ContractDto(
        Long id,
        Long employeeId,
        BigDecimal salary,
        BigDecimal igssDiscount,
        BigDecimal irtraDiscount,
        ContractEntity.TerminationReason terminationReason,
        String terminationDescription,
        LocalDate startDate,
        LocalDate endDate,
        Instant createdAt,
        Instant updatedAt
) {

    public static ContractDto fromEntity(ContractEntity entity) {
        return ContractDto
                .builder()
                .id(entity.getId())
                .employeeId(entity.getEmployee().getId())
                .salary(entity.getSalary())
                .igssDiscount(entity.getIgssDiscount())
                .irtraDiscount(entity.getIrtraDiscount())
                .terminationReason(entity.getTerminationReason())
                .terminationDescription(entity.getTerminationDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}



