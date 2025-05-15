package org.ehealth.ward.domain.dto.ward.admission;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddAdmissionDto(
        @JsonFormat(pattern = "yyyy-MM-dd") @NotNull LocalDate admissionDate,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate dischargeDate,
        Long roomId) {
}
