package org.ehealth.hr.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.dto.FinishContract;
import org.ehealth.hr.domain.dto.*;
import org.ehealth.hr.domain.dto.reports.HistoryEmployeeContractsDto;
import org.ehealth.hr.domain.dto.reports.ReportEmployeeContracts;
import org.ehealth.hr.domain.entity.ContractEntity;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.exception.RequestConflictException;
import org.ehealth.hr.domain.exception.ValueNotFoundException;
import org.ehealth.hr.repository.ContractRepository;
import org.ehealth.hr.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService implements IContractService {

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public ContractEntity finishContract(FinishContract finishContract) {
        ContractEntity contract = this.contractRepository.findById(finishContract.idContract())
                .orElseThrow(() -> new ValueNotFoundException("El contrato que se intenta finalizar no existe en planilla"));

        // Validar que la fecha de finalización no sea anterior a la fecha de inicio
        if (contract.getStartDate().isAfter(finishContract.date())) {
            throw new RequestConflictException("No se puede finalizar el contrato con una fecha anterior a su inicio.");
        }

        contract.setEndDate(finishContract.date());
        contract.setTerminationReason(finishContract.terminationReason());
        contract.setTerminationDescription(finishContract.description());
        return contractRepository.save(contract);
    }


    @Override
    public void createContractWithEmployee(CreateEmployeeDto dto, EmployeeEntity employee) {
        LocalDate startDate = dto.startDate();
        LocalDate today = LocalDate.now();

        // Validar que la fecha de inicio no sea futura
        if (startDate.isAfter(today)) {
            throw new RequestConflictException("No se puede registrar un contrato con una fecha de inicio mayor a la fecha actual.");
        }

        contractRepository.save(ContractEntity.builder()
                .salary(dto.salary())
                .igssDiscount(dto.igssDiscount())
                .irtraDiscount(dto.irtraDiscount())
                .startDate(startDate)
                .employee(employee)
                .build());
    }


    @Override
    public ContractDto getContractByEmployeeId(Long employeeId) {
        return contractRepository.findFirstByEmployeeIdOrderByCreatedAtDesc(employeeId, ContractDto.class)
                .orElseThrow(() -> new ValueNotFoundException("El empleado no tiene contratos registrados"));
    }

    @Override
    @Transactional
    public void createNewContract(NewContractDto newContractDto) {
        EmployeeEntity employee = this.employeeRepository.findById(newContractDto.idEmployee())
                .orElseThrow(() -> new ValueNotFoundException("El empleado no existe en planilla"));

        ContractEntity previousContract = this.contractRepository.findById(newContractDto.idContract())
                .orElseThrow(() -> new ValueNotFoundException("El contrato antiguo no existe en planilla"));

        // Calcular fecha de inicio: hoy
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.minusDays(1);

        // solo si el contrato esta vigenete
        if (previousContract.getTerminationReason() == null) {
            if (previousContract.getStartDate().isAfter(endDate)) {
                throw new RequestConflictException("No se puede finalizar el contrato con una fecha anterior a su inicio.");
            }

            previousContract.setEndDate(today.minusDays(1));
            previousContract.setTerminationReason(ContractEntity.TerminationReason.NUEVO_CONTRATO);
            previousContract.setTerminationDescription("El contrato terminó por un nuevo contrato registrado el " + today);
            contractRepository.save(previousContract);
        }

        // Crear nuevo contrato
        ContractEntity newContract = ContractEntity.builder()
                .salary(newContractDto.salary())
                .igssDiscount(newContractDto.igssDiscount())
                .irtraDiscount(newContractDto.irtraDiscount())
                .startDate(today)
                .employee(employee)
                .build();

        contractRepository.save(newContract);
    }

    @Override
    @Transactional
    public void finishContract(Long idContract, FinishContractDto finishContractDto) {
        ContractEntity contract = this.contractRepository.findById(idContract)
                .orElseThrow(() -> new ValueNotFoundException("El contrato no existe en planilla"));

        LocalDate today = LocalDate.now();

        // Validar que no se pueda finalizar con una fecha anterior a la de inicio
        if (contract.getStartDate().isAfter(today)) {
            throw new RequestConflictException("No se puede finalizar el contrato con una fecha anterior a su inicio.");
        }

        FinishContract finishContract = FinishContract
                .builder()
                .terminationReason(ContractEntity.TerminationReason.FIN_CONTRATO)
                .description(finishContractDto.description())
                .idContract(idContract)
                .date(today)
                .build();

        this.finishContract(finishContract);
    }

    @Override
    @Transactional
    public void updateSalary(Long idContract, UpdateSalaryDto updateSalaryDto) {
        FinishContract finishContract;
        if (updateSalaryDto.isIncrement()){
            finishContract = FinishContract
                    .builder()
                    .terminationReason(ContractEntity.TerminationReason.AUMENTO_SALARIAL)
                    .description("El contrato terminó por aumento salarial")
                    .idContract(idContract)
                    .date(LocalDate.now().minusDays(1))
                    .build();
        }else{
            finishContract = FinishContract
                    .builder()
                    .terminationReason(ContractEntity.TerminationReason.REDUCCION_SALARIAL)
                    .description("El contrato terminó por reduccion salarial")
                    .idContract(idContract)
                    .date(LocalDate.now().minusDays(1))
                    .build();
        }
        ContractEntity contract = this.finishContract(finishContract);

        ContractEntity newContract = ContractEntity.builder()
                .salary(updateSalaryDto.salary())
                .igssDiscount(contract.getIgssDiscount())
                .irtraDiscount(contract.getIrtraDiscount())
                .startDate(LocalDate.now())
                .employee(contract.getEmployee())
                .build();

        contractRepository.save(newContract);
    }

    @Override
    @Transactional
    public void dismissalWork(Long idContract, FinishContractDto finishContractDto) {
        ContractEntity contract = this.contractRepository.findById(idContract)
                .orElseThrow(() -> new ValueNotFoundException("El contrato no existe en planilla"));

        LocalDate today = LocalDate.now();

        // Validar que la fecha de despido no sea anterior a la fecha de inicio
        if (contract.getStartDate().isAfter(today)) {
            throw new RequestConflictException("No se puede despedir al empleado con una fecha anterior al inicio del contrato.");
        }

        FinishContract finishContract = FinishContract
                .builder()
                .terminationReason(ContractEntity.TerminationReason.DESPIDO)
                .description(finishContractDto.description())
                .idContract(idContract)
                .date(today)
                .build();

        this.finishContract(finishContract);
    }


    @Override
    public List<ContractDto> findAllContractsOrderedByCreationDate(Long employeeId) {
        return contractRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employeeId,ContractDto.class);
    }

    @Override
    public List<EmployeeDto> findAllEmployees(Long areaId) {
        // Obtener lista de empleados filtrando por área si aplica

        return (areaId == null || areaId <= 0)
                ? this.employeeRepository.findAllByOrderByCreatedAtDesc(EmployeeDto.class)
                : this.employeeRepository.findAllByAreaIdOrderByCreatedAtDesc(areaId, EmployeeDto.class);
    }

    @Override
    public ReportEmployeeContracts constructReport(List<EmployeeDto> employees, List<ContractDto> contracts ) {
        // Agrupar contratos por ID de empleado
        Map<Long, List<ContractDto>> contractsByEmployeeId = contracts.stream()
                .collect(Collectors.groupingBy(ContractDto::employeeId));

        // Mapear solo empleados que tengan al menos un contrato
        List<HistoryEmployeeContractsDto> history = employees.stream()
                .filter(emp -> contractsByEmployeeId.containsKey(emp.id())) // solo empleados con contratos
                .map(emp -> HistoryEmployeeContractsDto.builder()
                        .id(emp.id())
                        .fullName(emp.fullName())
                        .cui(emp.cui())
                        .email(emp.email())
                        .areaName(emp.areaName())
                        .contracts(contractsByEmployeeId.get(emp.id()))
                        .build())
                .toList();

        // Devolver reporte
        return ReportEmployeeContracts.builder()
                .report(history)
                .build();
    }

    @Override
    @Transactional
    public ReportEmployeeContracts reportEmployeeContracts(Long areaId, LocalDate startDate, LocalDate endDate) {

        List<EmployeeDto> employees = findAllEmployees(areaId);

        // Obtener lista de contratos, filtrando por fechas si se proporcionan
        List<ContractDto> contracts = (startDate == null || endDate == null)
                ? this.contractRepository.findAllByOrderByCreatedAtDesc(ContractDto.class)
                : this.contractRepository.findAllContractsBetweenDates(startDate, endDate);

        return this.constructReport(employees, contracts);

    }

    @Override
    public ReportEmployeeContracts reportTerminatedContracts(Long areaId, LocalDate startDate, LocalDate endDate){
        List<EmployeeDto> employees = findAllEmployees(areaId);

        // Obtener lista de contratos, filtrando por fechas si se proporcionan
        List<ContractDto> contracts = (startDate == null || endDate == null)
                ? this.contractRepository.findTerminatedContracts()
                : this.contractRepository.findTerminatedContractsBetweenDates(startDate, endDate);

        return this.constructReport(employees, contracts);

    }


}
