package org.ehealth.ward.domain.dto.ward.patient;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddPatientDto(
        @NotBlank String fullName,
        @NotBlank String cui,
        @JsonFormat(pattern = "yyyy-MM-dd") @Past LocalDate birthDate,
        String phone,
        String email) {
}
