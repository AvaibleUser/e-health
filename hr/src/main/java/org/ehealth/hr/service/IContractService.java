package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.FinishContract;
import org.ehealth.hr.domain.dto.*;
import org.ehealth.hr.domain.entity.ContractEntity;
import org.ehealth.hr.domain.entity.EmployeeEntity;

import java.util.List;

public interface IContractService {
    ContractEntity finishContract(FinishContract finishContract);
    void createContractWithEmployee(CreateEmployeeDto dto, EmployeeEntity employee);
    ContractDto getContractByEmployeeId(Long employeeId);
    void createNewContract(NewContractDto newContractDto);
    void finishContract(Long idContract, FinishContractDto finishContractDto);
    void updateSalary(Long idContract, UpdateSalaryDto updateSalaryDto);
    void dismissalWork(Long idContract, FinishContractDto finishContractDto);
    List<ContractDto> findAllContractsOrderedByCreationDate(Long employeeId) ;
}
