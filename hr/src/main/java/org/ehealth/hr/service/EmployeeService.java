package org.ehealth.hr.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.ehealth.hr.client.PatientClient;
import org.ehealth.hr.domain.dto.ContractDto;
import org.ehealth.hr.domain.dto.CreateEmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeResponseDto;
import org.ehealth.hr.domain.dto.reports.AssignedDto;
import org.ehealth.hr.domain.dto.reports.AssignedEmployeeReportDto;
import org.ehealth.hr.domain.dto.reports.HistoryAssignedEmployee;
import org.ehealth.hr.domain.dto.reports.ReportAssignedEmployeeDto;
import org.ehealth.hr.domain.entity.AreaEntity;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.exception.RequestConflictException;
import org.ehealth.hr.domain.exception.ValueNotFoundException;
import org.ehealth.hr.repository.AreaRepository;
import org.ehealth.hr.repository.ContractRepository;
import org.ehealth.hr.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AreaRepository areaRepository;
    private final IContractService contractService;
    private final IVacationService vacationService;
    private final PatientClient patientClient;
    private final ContractRepository contractRepository;


    @Override
    public EmployeeResponseDto createEmployee(CreateEmployeeDto dto) {
        // Validar que no exista otro con mismo CUI o email
        if (employeeRepository.existsByEmail(dto.email()) || employeeRepository.existsByCui(dto.cui())) {
            throw new RequestConflictException("Ya existe un empleado con el CUI o correo electrónico proporcionado.");
        }

        // Obtener el área asociada
        AreaEntity area = areaRepository.findById(dto.area())
                .orElseThrow(() -> new RequestConflictException("Área no encontrada con ID: " + dto.area()));

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

    @Override
    public EmployeeDto findEmployeeByCui(String cui) {
        return employeeRepository.findByCui(cui, EmployeeDto.class)
                .orElseThrow(() -> new ValueNotFoundException("El empleado que se intenta buscar no existe"));
    }

    @Override
    public List<EmployeeDto> findAllEmployeesOrdered() {
        return employeeRepository.findAllByOrderByCreatedAtDesc(EmployeeDto.class);
    }

    @Override
    public List<EmployeeDto> findEmployeesByArea(Long areaId) {
        return employeeRepository.findAllByAreaIdOrderByCreatedAtDesc(areaId, EmployeeDto.class);
    }

    @Override
    public ReportAssignedEmployeeDto getReportAssignedEmployeeInRange(Integer filter, String startDate, String endDate) {
        // 1. Obtener todos los empleados del área "Medicos"
        List<EmployeeDto> employees = employeeRepository.findAllByIsSpecialistFalseOrderByCreatedAtDesc(EmployeeDto.class).stream()
                .filter(emp -> "Medicos".equalsIgnoreCase(emp.areaName()))
                .toList();

        // 2. Obtener asignaciones a través del cliente
        List<AssignedEmployeeReportDto> assignedEmployees;
        try {
            assignedEmployees = this.patientClient.getDoctorsAssignedReport(startDate, endDate);
        } catch (FeignException e) {
            e.printStackTrace();
            throw new RequestConflictException("No se pudo obtener el reporte de asignaciones, intente más tarde");
        }

        // 3. Agrupar las asignaciones por empleadoId
        Map<Long, List<AssignedEmployeeReportDto>> groupedByEmployee = assignedEmployees.stream()
                .collect(Collectors.groupingBy(AssignedEmployeeReportDto::employeeId));

        // 4. Filtrar empleados y construir historial según el filtro
        List<HistoryAssignedEmployee> historyList = employees.stream()
                .filter(emp -> {
                    List<AssignedEmployeeReportDto> assignments = groupedByEmployee.get(emp.id());
                    if (assignments == null || assignments.isEmpty()) return false;

                    boolean hasOngoing = assignments.stream().anyMatch(a -> a.dischargeDate() == null);
                    boolean hasAllFinished = assignments.stream().allMatch(a -> a.dischargeDate() != null);

                    return switch (filter) {
                        case 1 -> hasOngoing;                      // Solo si tiene alguna admisión vigente
                        case 2 -> !hasOngoing && hasAllFinished;   // Solo si no tiene admisiones vigentes
                        default -> true;                           // Todos los empleados
                    };
                })
                .map(emp -> {
                    List<AssignedDto> filteredAssignments = groupedByEmployee.get(emp.id()).stream()
                            .filter(a -> {
                                if (filter == 1) return a.dischargeDate() == null; // Solo vigentes
                                if (filter == 2) return a.dischargeDate() != null; // Solo terminadas
                                return true; // Todos
                            })
                            .map(a -> AssignedDto.builder()
                                    .admissionDate(a.admissionDate())
                                    .dischargeDate(a.dischargeDate())
                                    .fullName(a.fullName())
                                    .cui(a.cui())
                                    .build())
                            .toList();

                    // Obtener el último contrato del empleado
                    ContractDto contract = contractRepository
                            .findFirstByEmployeeIdOrderByCreatedAtDesc(emp.id(), ContractDto.class)
                            .orElse(null);

                    return HistoryAssignedEmployee.builder()
                            .id(emp.id())
                            .fullName(emp.fullName())
                            .cui(emp.cui())
                            .email(emp.email())
                            .contract(contract)
                            .assignedList(filteredAssignments)
                            .build();
                })
                .toList();

        return ReportAssignedEmployeeDto.builder()
                .report(historyList)
                .build();
    }


}
