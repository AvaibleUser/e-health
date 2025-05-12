package org.ehealth.ward.domain.dto.ward.employee;

import org.ehealth.ward.domain.dto.client.employee.EmployeeDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Builder;

@Builder(toBuilder = true)
public record CompleteEmployeeDto(
        @JsonUnwrapped @JsonIgnoreProperties( {
                "id", "createdAt", "updatedAt" }) EmployeeDto employee,
        @JsonUnwrapped AssignedEmployeeDto assignedEmployee){
}
