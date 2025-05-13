package org.ehealth.ward.domain.dto.ward.admission;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddAdmissionDto(
        @JsonFormat(pattern = "yyyy-MM-dd") @NotNull @FutureOrPresent LocalDate admissionDate,
        @JsonFormat(pattern = "yyyy-MM-dd") @Future LocalDate dischargeDate,
        Long roomId) {
}
