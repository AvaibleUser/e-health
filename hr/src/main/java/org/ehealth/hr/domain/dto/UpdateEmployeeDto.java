package org.ehealth.hr.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateEmployeeDto(
        @NotBlank
        String phone,
        @NotBlank @Email String email,
        @Positive Long areId

) {
}
