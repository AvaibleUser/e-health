package org.ehealth.hr.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record FinishContractDto(
        @NotBlank
        String description
) {
}
