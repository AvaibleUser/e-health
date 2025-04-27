package org.ehealth.gatekeeper.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddUserDto(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String password) {
}
