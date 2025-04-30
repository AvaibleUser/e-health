package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.EmployeeDto;
import org.ehealth.hr.domain.exception.ValueNotFoundException;
import org.ehealth.hr.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDto findEmployeeByCui(String cui) {
        return employeeRepository.findByCui(cui, EmployeeDto.class)
                .orElseThrow(() -> new ValueNotFoundException("El empleado que se intenta buscar no existe"));
    }
}
