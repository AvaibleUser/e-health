package org.ehealth.ward.domain.dto.or.surgery;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddSurgeryDto(
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate performedDate,
        String description,
        @NotNull @Positive Long tariffId) {
}
