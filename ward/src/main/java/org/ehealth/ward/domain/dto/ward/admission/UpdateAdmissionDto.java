package org.ehealth.ward.domain.dto.ward.admission;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateAdmissionDto(
        @JsonFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> dischargeDate,
        Optional<Long> roomId) {
}
