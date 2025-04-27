package org.ehealth.gatekeeper.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmUserDto(
        @NotBlank String email,
        @NotBlank String code) {
}
