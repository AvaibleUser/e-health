package org.ehealth.ward.domain.dto.ward.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record CreateRoom(
        @NotBlank(message = "El numero no puede estar en blanco")
        @Size(max = 20, message = "El area tiene un maximo de 20 caracteres")
        String number,
        @Positive(message = "El costo por dia debe ser mayor a cero")
        BigDecimal costPerDay

) {
}
