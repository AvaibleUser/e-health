package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.CreateEmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeResponseDto;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.dto.EmployeeDto;

public interface IEmployeeService {

    EmployeeResponseDto createEmployee(CreateEmployeeDto dto);
    EmployeeDto findEmployeeByCui(String cui);
}
