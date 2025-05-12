package org.ehealth.ward.domain.dto.ward.patient;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Past;

public record UpdatePatientDto(
        Optional<String> fullName,
        @JsonFormat(pattern = "yyyy-MM-dd") Optional<@Past LocalDate> dateOfBirth,
        Optional<String> phone,
        Optional<String> email) {
}
