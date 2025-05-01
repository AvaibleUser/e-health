package org.ehealth.hr.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.dto.CreateEmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeResponseDto;
import org.ehealth.hr.domain.entity.AreaEntity;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.exception.ValueNotFoundException;
import org.ehealth.hr.repository.AreaRepository;
import org.ehealth.hr.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AreaRepository areaRepository;
    private final IContractService contractService;
    private final IVacationService vacationService;

    @Override
    public EmployeeResponseDto createEmployee(CreateEmployeeDto dto) {
        // Validar que no exista otro con mismo CUI o email
        if (employeeRepository.existsByEmail(dto.email()) || employeeRepository.existsByCui(dto.cui())) {
            throw new IllegalArgumentException("Ya existe un empleado con el CUI o correo electrónico proporcionado.");
        }

        // Obtener el área asociada
        AreaEntity area = areaRepository.findById(dto.area())
                .orElseThrow(() -> new IllegalArgumentException("Área no encontrada con ID: " + dto.area()));

        // Crear el empleado
        EmployeeEntity employee = employeeRepository.save(EmployeeEntity.builder()
                .fullName(dto.fullName())
                .cui(dto.cui())
                .phone(dto.phone())
                .email(dto.email())
                .area(area)
                .isSpecialist(dto.isSpecialist())
                .build());


        // validar si es medico especialista
        if (employee.isSpecialist()){
            return EmployeeResponseDto.fromEntity(employee);
        }

        //registrar el contrato
        this.contractService.createContractWithEmployee(dto, employee);

        // Crear vacaciones con lógica interna
        this.vacationService.createVacationWithEmployee(employee,dto.startDate());

        return EmployeeResponseDto.fromEntity(employee);
    }

    public EmployeeDto findEmployeeByCui(String cui) {
        return employeeRepository.findByCui(cui, EmployeeDto.class)
                .orElseThrow(() -> new ValueNotFoundException("El empleado que se intenta buscar no existe"));
    }
}
