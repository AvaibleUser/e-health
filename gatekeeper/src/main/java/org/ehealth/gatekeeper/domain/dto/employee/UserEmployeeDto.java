package org.ehealth.gatekeeper.domain.dto.employee;

import org.ehealth.gatekeeper.domain.dto.UserDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserEmployeeDto(
        // User
        @JsonUnwrapped @JsonIgnoreProperties( {
                "cui" }) UserDto user,
        // Long id,
        // boolean active,
        // String roleName,
        // Instant createdAt,
        // Instant updatedAt,
        // employee
        // Long employeeId,
        // String fullName,
        // String cui,
        // String phone,
        // String email,
        // boolean isSpecialist,
        // String areaName
        @JsonUnwrapped @JsonIgnoreProperties({ "id", "email", "createdAt", "updatedAt" }) EmployeeDto employee){
}
