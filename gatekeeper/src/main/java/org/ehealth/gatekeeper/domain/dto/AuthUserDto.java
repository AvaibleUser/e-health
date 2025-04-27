package org.ehealth.gatekeeper.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthUserDto(
        @NotBlank String email,
        @NotBlank String password) {
}
