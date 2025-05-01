package org.ehealth.hr.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.dto.CreateEmployeeDto;
import org.ehealth.hr.domain.entity.ContractEntity;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.repository.ContractRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractService implements IContractService {

    private final ContractRepository contractRepository;

    @Override
    public void createContractWithEmployee(CreateEmployeeDto dto, EmployeeEntity employee) {

        contractRepository.save(ContractEntity.builder()
                .salary(dto.salary())
                .igssDiscount(dto.igssDiscount())
                .irtraDiscount(dto.irtraDiscount())
                .startDate(dto.startDate())
                .employee(employee)
                .build());
    }
}
