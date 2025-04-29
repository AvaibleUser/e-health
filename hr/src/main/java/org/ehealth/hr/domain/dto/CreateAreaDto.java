package org.ehealth.hr.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder(toBuilder = true)
public record CreateAreaDto(
        @NotBlank(message = "El nombre del area no puede estar en blanco")
        @Size(max = 100, message = "El area tiene un maximo de 100 caracteres")
        String name
) {
}
