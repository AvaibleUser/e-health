package org.ehealth.gatekeeper.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddUserDto(
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String fullName,
        @NotBlank String cui) {
}
