package org.ehealth.rx.domain.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder(toBuilder = true)
public record CreatePurchacheDto(
        @Positive
        Integer quantity
) {
}
