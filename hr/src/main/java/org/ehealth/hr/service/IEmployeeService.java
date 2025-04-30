package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.EmployeeDto;

public interface IEmployeeService {

    EmployeeDto findEmployeeByCui(String cui);
}
