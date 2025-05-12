package org.ehealth.ward.domain.dto.or.surgery;

import java.time.LocalDate;
import java.util.Optional;

import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateSurgeryDto(
        Optional<LocalDate> performedDate,
        Optional<String> description) {
}
