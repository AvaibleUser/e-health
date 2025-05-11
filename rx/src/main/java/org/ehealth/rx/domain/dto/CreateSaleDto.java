package org.ehealth.rx.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record CreateSaleDto(
        @Positive
        Long patientId,
        List<ItemSaleDto> items
) {
}
