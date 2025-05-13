package org.ehealth.hr.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record CreateEmployeeDto(
        //employee
        @NotBlank String fullName,
        @NotBlank String cui,
        @NotBlank String phone,
        @NotBlank @Email String email,
        @Positive Long area,
        @NotNull boolean isSpecialist,
        // contract
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @Positive BigDecimal salary,
        @Positive BigDecimal igssDiscount,
        @Positive BigDecimal irtraDiscount
) {
}
