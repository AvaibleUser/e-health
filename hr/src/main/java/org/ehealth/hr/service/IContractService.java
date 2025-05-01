package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.CreateEmployeeDto;
import org.ehealth.hr.domain.entity.EmployeeEntity;

public interface IContractService {
    void createContractWithEmployee(CreateEmployeeDto dto, EmployeeEntity employee);
}
