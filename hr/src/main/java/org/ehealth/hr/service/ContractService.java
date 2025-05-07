package org.ehealth.hr.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.dto.FinishContract;
import org.ehealth.hr.domain.dto.*;
import org.ehealth.hr.domain.dto.reports.HistoryEmployeeContractsDto;
import org.ehealth.hr.domain.dto.reports.ReportEmployeeContracts;
import org.ehealth.hr.domain.entity.ContractEntity;
import org.ehealth.hr.domain.entity.EmployeeEntity;
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

        contract.setEndDate(finishContract.date());
        contract.setTerminationReason(finishContract.terminationReason());
        contract.setTerminationDescription(finishContract.description());
        return contractRepository.save(contract);
    }

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

        // solo si el contrato esta vigenete
        if (previousContract.getTerminationReason() == null) {
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
        FinishContract finishContract = FinishContract
                .builder()
                .terminationReason(ContractEntity.TerminationReason.FIN_CONTRATO)
                .description(finishContractDto.description())
                .idContract(idContract)
                .date(LocalDate.now())
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
    public void dismissalWork(Long idContract, FinishContractDto finishContractDto){
        FinishContract finishContract = FinishContract
                .builder()
                .terminationReason(ContractEntity.TerminationReason.DESPIDO)
                .description(finishContractDto.description())
                .idContract(idContract)
                .date(LocalDate.now())
                .build();

        this.finishContract(finishContract);
    }

    @Override
    public List<ContractDto> findAllContractsOrderedByCreationDate(Long employeeId) {
        return contractRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employeeId,ContractDto.class);
    }

    @Override
    @Transactional
    public ReportEmployeeContracts reportEmployeeContracts(Long areaId, LocalDate startDate, LocalDate endDate) {
        // 1. Obtener lista de empleados filtrando por área si aplica
        List<EmployeeDto> employees = (areaId == null || areaId <= 0)
                ? this.employeeRepository.findAllByOrderByCreatedAtDesc(EmployeeDto.class)
                : this.employeeRepository.findAllByAreaIdOrderByCreatedAtDesc(areaId, EmployeeDto.class);

        // 2. Obtener lista de contratos, filtrando por fechas si se proporcionan
        List<ContractDto> contracts = (startDate == null || endDate == null)
                ? this.contractRepository.findAllByOrderByCreatedAtDesc(ContractDto.class)
                : this.contractRepository.findAllContractsBetweenDates(startDate, endDate);

        // 3. Agrupar contratos por ID de empleado
        Map<Long, List<ContractDto>> contractsByEmployeeId = contracts.stream()
                .collect(Collectors.groupingBy(ContractDto::employeeId));

        // 4. Mapear solo empleados que tengan al menos un contrato
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

        // 5. Devolver reporte
        return ReportEmployeeContracts.builder()
                .report(history)
                .build();
    }

}
