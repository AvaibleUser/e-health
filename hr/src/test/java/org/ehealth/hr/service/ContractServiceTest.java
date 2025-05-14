package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.*;
import org.ehealth.hr.domain.dto.reports.HistoryEmployeeContractsDto;
import org.ehealth.hr.domain.dto.reports.ReportEmployeeContracts;
import org.ehealth.hr.domain.entity.AreaEntity;
import org.ehealth.hr.domain.entity.ContractEntity;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.exception.RequestConflictException;
import org.ehealth.hr.domain.exception.ValueNotFoundException;
import org.ehealth.hr.repository.ContractRepository;
import org.ehealth.hr.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    private static final Long VALID_CONTRACT_ID = 1L;
    private static final Long INVALID_CONTRACT_ID = 999L;
    private static final LocalDate FINISH_DATE = LocalDate.of(2023, 12, 31);
    private static final String TERMINATION_DESCRIPTION = "Contract finished by mutual agreement";

    private static final BigDecimal VALID_SALARY = new BigDecimal("5000.00");
    private static final BigDecimal VALID_IGSS_DISCOUNT = new BigDecimal("250.00");
    private static final BigDecimal VALID_IRTRA_DISCOUNT = new BigDecimal("100.00");
    private static final LocalDate VALID_START_DATE = LocalDate.of(2023, 1, 1);
    private static final String EMPLOYEE_EMAIL = "employee@company.com";
    private static final String EMPLOYEE_CUI = "1234567890123";

    private static final Long EMPLOYEE_WITH_CONTRACT_ID = 1L;
    private static final Long EMPLOYEE_WITHOUT_CONTRACT_ID = 2L;
    private static final Instant CONTRACT_CREATED_AT = Instant.parse("2023-01-01T00:00:00Z");

    private static final Long VALID_EMPLOYEE_ID = 1L;
    private static final Long INVALID_EMPLOYEE_ID = 999L;
    private static final BigDecimal NEW_SALARY = new BigDecimal("6000.00");
    private static final BigDecimal NEW_IGSS_DISCOUNT = new BigDecimal("300.00");
    private static final BigDecimal NEW_IRTRA_DISCOUNT = new BigDecimal("150.00");

    private static final BigDecimal UPDATED_SALARY = new BigDecimal("7000.00");
    private static final BigDecimal CURRENT_IGSS_DISCOUNT = new BigDecimal("350.00");
    private static final BigDecimal CURRENT_IRTRA_DISCOUNT = new BigDecimal("175.00");

    private static final Long EMPLOYEE_WITH_CONTRACTS_ID = 1L;
    private static final Long EMPLOYEE_WITHOUT_CONTRACTS_ID = 2L;

    private static final Long VALID_AREA_ID = 1L;
    private static final String VALID_AREA_NAME = "Farmacia";
    private static final Long INVALID_AREA_ID = -1L;
    private static final Instant EMPLOYEE_CREATED_AT = Instant.parse("2023-01-01T00:00:00Z");

    private static final String AREA_IT = "IT Department";
    private static final String AREA_HR = "HR Department";
    private static final String VALID_CUI = "1234567890123";

    private static final LocalDate START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 12, 31);
    private static final String AREA_NAME = "IT Department";

    private static final String NAME_EMPLOYEE = "Elvis Aguilar";


    @Mock
    private ContractRepository contractRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ContractService contractService;

    @BeforeEach
    void setUp() {

    }


    /**
     * tests function finishContract
     */
    @Test
    void shouldUpdateContractWithFinishDetailsWhenContractExists() {
        // given
        ContractEntity existingContract = buildValidContract();
        FinishContract finishRequest = buildFinishContractRequest(VALID_CONTRACT_ID);

        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(existingContract));
        given(contractRepository.save(any(ContractEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ContractEntity result = contractService.finishContract(finishRequest);

        // then
        assertThat(result.getEndDate()).isEqualTo(FINISH_DATE);
        assertThat(result.getTerminationReason()).isEqualTo(ContractEntity.TerminationReason.FIN_CONTRATO);
        assertThat(result.getTerminationDescription()).isEqualTo(TERMINATION_DESCRIPTION);

        then(contractRepository).should().findById(VALID_CONTRACT_ID);
        then(contractRepository).should().save(existingContract);
    }

    @Test
    void shouldThrowValueNotFoundExceptionWhenContractDoesNotExist() {
        // given
        FinishContract finishRequest = buildFinishContractRequest(INVALID_CONTRACT_ID);

        given(contractRepository.findById(INVALID_CONTRACT_ID))
                .willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> contractService.finishContract(finishRequest))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessage("El contrato que se intenta finalizar no existe en planilla");

        then(contractRepository).should().findById(INVALID_CONTRACT_ID);
        then(contractRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void shouldPersistAllFinishContractFieldsWhenProvided() {
        // given
        ContractEntity existingContract = buildValidContract();
        FinishContract finishRequest = FinishContract
                .builder()
                .description("Terminated due to company restructuring")
                .terminationReason(ContractEntity.TerminationReason.DESPIDO)
                .date(FINISH_DATE)
                .idContract(VALID_CONTRACT_ID)
                .build();

        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(existingContract));
        given(contractRepository.save(any(ContractEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ContractEntity result = contractService.finishContract(finishRequest);

        // then
        assertThat(result.getEndDate()).isEqualTo(FINISH_DATE);
        assertThat(result.getTerminationReason()).isEqualTo(ContractEntity.TerminationReason.DESPIDO);
        assertThat(result.getTerminationDescription()).isEqualTo("Terminated due to company restructuring");
    }

    @Test
    void shouldThrowRequestConflictExceptionWhenFinishDateIsBeforeStartDate() {
        // given
        ContractEntity existingContract = buildValidContract()
                .toBuilder()
                .startDate(LocalDate.of(2023, 1, 15)) // Fecha de inicio posterior
                .build();

        FinishContract invalidFinishRequest = FinishContract.builder()
                .date(LocalDate.of(2023, 1, 10)) // Fecha anterior al inicio
                .idContract(VALID_CONTRACT_ID)
                .build();

        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(existingContract));

        // when / then
        assertThatThrownBy(() -> contractService.finishContract(invalidFinishRequest))
                .isInstanceOf(RequestConflictException.class)
                .hasMessage("No se puede finalizar el contrato con una fecha anterior a su inicio.");

        then(contractRepository).should().findById(VALID_CONTRACT_ID);
        then(contractRepository).shouldHaveNoMoreInteractions();
    }

    /**
     * tests function createContractWithEmployee
     */
    @Test
    void shouldCreateContractWithAllFieldsWhenValidDataProvided() {
        // given
        CreateEmployeeDto validDto = buildValidCreateEmployeeDto();
        EmployeeEntity validEmployee = buildValidEmployee();
        ContractEntity expectedContract = buildExpectedContract(validDto, validEmployee);

        given(contractRepository.save(any(ContractEntity.class)))
                .willReturn(expectedContract);

        // when
        contractService.createContractWithEmployee(validDto, validEmployee);

        // then
        ArgumentCaptor<ContractEntity> contractCaptor = ArgumentCaptor.forClass(ContractEntity.class);
        then(contractRepository).should().save(contractCaptor.capture());

        ContractEntity savedContract = contractCaptor.getValue();
        assertThat(savedContract.getSalary()).isEqualTo(VALID_SALARY);
        assertThat(savedContract.getIgssDiscount()).isEqualTo(VALID_IGSS_DISCOUNT);
        assertThat(savedContract.getIrtraDiscount()).isEqualTo(VALID_IRTRA_DISCOUNT);
        assertThat(savedContract.getStartDate()).isEqualTo(VALID_START_DATE);
        assertThat(savedContract.getEmployee()).isEqualTo(validEmployee);
    }

    @Test
    void shouldCreateContractWithOnlyRequiredFieldsWhenOptionalFieldsNull() {
        // given
        CreateEmployeeDto dtoWithNulls = buildValidCreateEmployeeDto().toBuilder()
                .igssDiscount(null)
                .irtraDiscount(null)
                .build();

        EmployeeEntity validEmployee = buildValidEmployee();

        // when
        contractService.createContractWithEmployee(dtoWithNulls, validEmployee);

        // then
        ArgumentCaptor<ContractEntity> contractCaptor = ArgumentCaptor.forClass(ContractEntity.class);
        then(contractRepository).should().save(contractCaptor.capture());

        ContractEntity savedContract = contractCaptor.getValue();
        assertThat(savedContract.getSalary()).isEqualTo(VALID_SALARY);
        assertThat(savedContract.getIgssDiscount()).isNull();
        assertThat(savedContract.getIrtraDiscount()).isNull();
        assertThat(savedContract.getStartDate()).isEqualTo(VALID_START_DATE);
    }

    @Test
    void shouldThrowExceptionWhenStartDateIsInFuture() {
        // given
        CreateEmployeeDto invalidDateDto = buildValidCreateEmployeeDto().toBuilder()
                .startDate(LocalDate.now().plusDays(1)) // Fecha futura
                .build();

        EmployeeEntity validEmployee = buildValidEmployee();

        // when / then
        assertThatThrownBy(() -> contractService.createContractWithEmployee(invalidDateDto, validEmployee))
                .isInstanceOf(RequestConflictException.class)
                .hasMessage("No se puede registrar un contrato con una fecha de inicio mayor a la fecha actual.");

        then(contractRepository).shouldHaveNoInteractions();
    }

    /**
     * tests function getContractByEmployeeId
     */
    @Test
    void shouldReturnLatestContractWhenEmployeeHasContracts() {
        // given
        ContractDto expectedContract = buildValidContractDto(EMPLOYEE_WITH_CONTRACT_ID);

        given(contractRepository.findFirstByEmployeeIdOrderByCreatedAtDesc(
                eq(EMPLOYEE_WITH_CONTRACT_ID), eq(ContractDto.class)))
                .willReturn(Optional.of(expectedContract));

        // when
        ContractDto result = contractService.getContractByEmployeeId(EMPLOYEE_WITH_CONTRACT_ID);

        // then
        assertThat(result).isEqualTo(expectedContract);
        then(contractRepository).should().findFirstByEmployeeIdOrderByCreatedAtDesc(
                EMPLOYEE_WITH_CONTRACT_ID, ContractDto.class);
    }

    @Test
    void shouldThrowValueNotFoundExceptionWhenEmployeeHasNoContracts() {
        // given
        given(contractRepository.findFirstByEmployeeIdOrderByCreatedAtDesc(
                eq(EMPLOYEE_WITHOUT_CONTRACT_ID), eq(ContractDto.class)))
                .willReturn(Optional.empty());


        // when / then
        assertThatThrownBy(() -> contractService.getContractByEmployeeId(EMPLOYEE_WITHOUT_CONTRACT_ID))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessage("El empleado no tiene contratos registrados");

        then(contractRepository).should().findFirstByEmployeeIdOrderByCreatedAtDesc(
                EMPLOYEE_WITHOUT_CONTRACT_ID, ContractDto.class);
    }

    /**
     * tests function createNewContract
     */
    @Test
    void shouldCreateNewContractAndTerminatePreviousOneWhenValidData() {
        // given
        NewContractDto validDto = buildValidNewContractDto();
        EmployeeEntity validEmployee = buildValidEmployee();
        ContractEntity previousContract = buildValidContract(VALID_CONTRACT_ID)
                .toBuilder()
                .terminationReason(null) // Contrato vigente
                .build();

        given(employeeRepository.findById(VALID_EMPLOYEE_ID))
                .willReturn(Optional.of(validEmployee));
        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(previousContract));
        given(contractRepository.save(any(ContractEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        contractService.createNewContract(validDto);

        // then - Capturar ambos contratos guardados
        ArgumentCaptor<ContractEntity> contractCaptor = ArgumentCaptor.forClass(ContractEntity.class);
        then(contractRepository).should(times(2)).save(contractCaptor.capture());

        List<ContractEntity> savedContracts = contractCaptor.getAllValues();

        // Separar contratos según si están terminados o no
        ContractEntity terminatedContract = savedContracts.stream()
                .filter(c -> c.getTerminationReason() != null)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No se encontró contrato terminado"));

        ContractEntity newContract = savedContracts.stream()
                .filter(c -> c.getTerminationReason() == null)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No se encontró contrato nuevo"));

        // Verificación del contrato terminado
        assertThat(terminatedContract.getEndDate()).isEqualTo(LocalDate.now().minusDays(1));
        assertThat(terminatedContract.getTerminationReason()).isEqualTo(ContractEntity.TerminationReason.NUEVO_CONTRATO);
        assertThat(terminatedContract.getTerminationDescription()).contains(LocalDate.now().toString());

        // Verificación del nuevo contrato
        assertThat(newContract.getSalary()).isEqualTo(NEW_SALARY);
        assertThat(newContract.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(newContract.getEmployee()).isEqualTo(validEmployee);
    }

    @Test
    void shouldCreateNewContractWithoutTerminatingPreviousWhenAlreadyTerminated() {
        // given
        NewContractDto validDto = buildValidNewContractDto();
        EmployeeEntity validEmployee = buildValidEmployee();
        ContractEntity terminatedContract = buildValidContract(VALID_CONTRACT_ID)
                .toBuilder()
                .terminationReason(ContractEntity.TerminationReason.FIN_CONTRATO)
                .build();

        given(employeeRepository.findById(VALID_EMPLOYEE_ID))
                .willReturn(Optional.of(validEmployee));
        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(terminatedContract));

        // when
        contractService.createNewContract(validDto);

        // then - No debe modificar el contrato anterior
        then(contractRepository).should(never()).save(terminatedContract);

        // then - Debe crear el nuevo contrato
        then(contractRepository).should().save(any(ContractEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        // given
        NewContractDto dto = buildValidNewContractDto().toBuilder()
                .idEmployee(INVALID_EMPLOYEE_ID)
                .build();

        given(employeeRepository.findById(INVALID_EMPLOYEE_ID))
                .willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> contractService.createNewContract(dto))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessage("El empleado no existe en planilla");
    }

    @Test
    void shouldThrowExceptionWhenPreviousContractNotFound() {
        // given
        NewContractDto dto = buildValidNewContractDto().toBuilder()
                .idContract(INVALID_CONTRACT_ID)
                .build();

        given(employeeRepository.findById(VALID_EMPLOYEE_ID))
                .willReturn(Optional.of(buildValidEmployee()));
        given(contractRepository.findById(INVALID_CONTRACT_ID))
                .willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> contractService.createNewContract(dto))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessage("El contrato antiguo no existe en planilla");
    }

    @Test
    void shouldThrowExceptionWhenPreviousContractStartDateIsAfterEndDate() {
        // given
        NewContractDto validDto = buildValidNewContractDto();
        EmployeeEntity validEmployee = buildValidEmployee();

        // Contrato con fecha de inicio posterior a la fecha de finalización (hoy-1)
        ContractEntity invalidContract = buildValidContract(VALID_CONTRACT_ID)
                .toBuilder()
                .startDate(LocalDate.now().plusDays(1)) // Fecha futura
                .terminationReason(null) // Contrato vigente
                .build();

        given(employeeRepository.findById(VALID_EMPLOYEE_ID))
                .willReturn(Optional.of(validEmployee));
        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(invalidContract));

        // when / then
        assertThatThrownBy(() -> contractService.createNewContract(validDto))
                .isInstanceOf(RequestConflictException.class)
                .hasMessage("No se puede finalizar el contrato con una fecha anterior a su inicio.");
    }

    /**
     * tests function finishContract
     */
    @Test
    void shouldDelegateToMainFinishContractMethodWithCorrectParameters() {
        // given
        Long contractId = VALID_CONTRACT_ID;
        FinishContractDto dto = buildFinishContractDto();
        ContractEntity expectedContract = buildValidContract(contractId);

        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(expectedContract));
        given(contractRepository.save(any(ContractEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        contractService.finishContract(contractId, dto);

        // then - Verificar que se llamó al método principal con los parámetros correctos
        ArgumentCaptor<ContractEntity> contractCaptor = ArgumentCaptor.forClass(ContractEntity.class);
        then(contractRepository).should().save(contractCaptor.capture());

        ContractEntity savedContract = contractCaptor.getValue();
        assertThat(savedContract.getEndDate()).isEqualTo(LocalDate.now());
        assertThat(savedContract.getTerminationReason()).isEqualTo(ContractEntity.TerminationReason.FIN_CONTRATO);
        assertThat(savedContract.getTerminationDescription()).isEqualTo(TERMINATION_DESCRIPTION);
    }

    @Test
    void shouldPropagateExceptionWhenMainMethodFails() {
        // given
        Long contractId = INVALID_CONTRACT_ID;
        FinishContractDto dto = buildFinishContractDto();

        given(contractRepository.findById(INVALID_CONTRACT_ID))
                .willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> contractService.finishContract(contractId, dto))
                .isInstanceOf(ValueNotFoundException.class);
    }

    @Test
    void shouldThrowRequestConflictExceptionWhenContractStartDateIsInFuture() {
        // given
        Long contractId = VALID_CONTRACT_ID;
        FinishContractDto dto = buildFinishContractDto();
        ContractEntity futureContract = buildValidContract(contractId)
                .toBuilder()
                .startDate(LocalDate.now().plusDays(1))
                .build();

        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(futureContract));

        // when / then
        assertThatThrownBy(() -> contractService.finishContract(contractId, dto))
                .isInstanceOf(RequestConflictException.class)
                .hasMessage("No se puede finalizar el contrato con una fecha anterior a su inicio.");

        then(contractRepository).should().findById(VALID_CONTRACT_ID);
        then(contractRepository).shouldHaveNoMoreInteractions();
    }

    /**
     * tests function updateSalary
     */
    @Test
    void shouldCreateNewContractWithIncreasedSalaryWhenIncrementIsTrue() {
        // given
        UpdateSalaryDto incrementDto = buildUpdateSalaryDto(true);
        ContractEntity terminatedContract = buildValidContract(VALID_CONTRACT_ID)
                .toBuilder()
                .igssDiscount(CURRENT_IGSS_DISCOUNT)
                .irtraDiscount(CURRENT_IRTRA_DISCOUNT)
                .employee(buildValidEmployee())
                .build();

        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(terminatedContract));
        given(contractRepository.save(any(ContractEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        contractService.updateSalary(VALID_CONTRACT_ID, incrementDto);

        // then - capturar ambas llamadas a save(...)
        ArgumentCaptor<ContractEntity> contractCaptor = ArgumentCaptor.forClass(ContractEntity.class);
        then(contractRepository).should(times(2)).save(contractCaptor.capture());

        List<ContractEntity> savedContracts = contractCaptor.getAllValues();

        // Separar contratos según terminación
        ContractEntity terminated = savedContracts.stream()
                .filter(c -> c.getTerminationReason() != null)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Contrato terminado no encontrado"));

        ContractEntity newContract = savedContracts.stream()
                .filter(c -> c.getTerminationReason() == null)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nuevo contrato no encontrado"));

        // Validar contrato terminado
        assertThat(terminated.getTerminationReason()).isEqualTo(ContractEntity.TerminationReason.AUMENTO_SALARIAL);
        assertThat(terminated.getEndDate()).isEqualTo(LocalDate.now().minusDays(1));

        // Validar nuevo contrato
        assertThat(newContract.getSalary()).isEqualTo(UPDATED_SALARY);
        assertThat(newContract.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(newContract.getIgssDiscount()).isEqualTo(CURRENT_IGSS_DISCOUNT);
        assertThat(newContract.getEmployee()).isEqualTo(terminatedContract.getEmployee());
    }

    @Test
    void shouldCreateNewContractWithDecreasedSalaryWhenIncrementIsFalse() {
        // given
        UpdateSalaryDto decrementDto = buildUpdateSalaryDto(false);
        ContractEntity terminatedContract = buildValidContract(VALID_CONTRACT_ID)
                .toBuilder()
                .igssDiscount(CURRENT_IGSS_DISCOUNT)
                .irtraDiscount(CURRENT_IRTRA_DISCOUNT)
                .employee(buildValidEmployee())
                .build();

        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(terminatedContract));
        given(contractRepository.save(any(ContractEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        contractService.updateSalary(VALID_CONTRACT_ID, decrementDto);

        // then - capturar ambas llamadas a save(...)
        ArgumentCaptor<ContractEntity> contractCaptor = ArgumentCaptor.forClass(ContractEntity.class);
        then(contractRepository).should(times(2)).save(contractCaptor.capture());

        List<ContractEntity> savedContracts = contractCaptor.getAllValues();

        // Separar contratos
        ContractEntity terminated = savedContracts.stream()
                .filter(c -> c.getTerminationReason() != null)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Contrato terminado no encontrado"));

        ContractEntity newContract = savedContracts.stream()
                .filter(c -> c.getTerminationReason() == null)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nuevo contrato no encontrado"));

        // Validar contrato terminado
        assertThat(terminated.getTerminationReason()).isEqualTo(ContractEntity.TerminationReason.REDUCCION_SALARIAL);

        // Validar nuevo contrato (si quieres, puedes agregar más validaciones)
        assertThat(newContract.getSalary()).isEqualTo(UPDATED_SALARY); // este campo debería reflejar el nuevo salario
        assertThat(newContract.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(newContract.getEmployee()).isEqualTo(terminatedContract.getEmployee());
    }

    @Test
    void shouldKeepDiscountsFromPreviousContractWhenCreatingNewOne() {
        // given
        UpdateSalaryDto updateDto = buildUpdateSalaryDto(true);
        ContractEntity previousContract = buildValidContract(VALID_CONTRACT_ID)
                .toBuilder()
                .igssDiscount(CURRENT_IGSS_DISCOUNT)
                .irtraDiscount(CURRENT_IRTRA_DISCOUNT)
                .build();

        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(previousContract));
        given(contractRepository.save(any(ContractEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        contractService.updateSalary(VALID_CONTRACT_ID, updateDto);

        // then
        ArgumentCaptor<ContractEntity> contractCaptor = ArgumentCaptor.forClass(ContractEntity.class);
        then(contractRepository).should(times(2)).save(contractCaptor.capture());

        List<ContractEntity> savedContracts = contractCaptor.getAllValues();

        ContractEntity newContract = savedContracts.stream()
                .filter(c -> c.getTerminationReason() == null)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nuevo contrato no encontrado"));

        assertThat(newContract.getIgssDiscount()).isEqualTo(CURRENT_IGSS_DISCOUNT);
        assertThat(newContract.getIrtraDiscount()).isEqualTo(CURRENT_IRTRA_DISCOUNT);
    }

    @Test
    void shouldThrowExceptionWhenContractNotFound() {
        // given
        UpdateSalaryDto updateDto = buildUpdateSalaryDto(true);

        given(contractRepository.findById(INVALID_CONTRACT_ID))
                .willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> contractService.updateSalary(INVALID_CONTRACT_ID, updateDto))
                .isInstanceOf(ValueNotFoundException.class);
    }

    /**
     * tests function dismissalWork
     */
    @Test
    void shouldTerminateContractWithDismissalReasonWhenValidInput() {
        // given
        Long contractId = VALID_CONTRACT_ID;
        FinishContractDto dto = buildFinishContractDto();
        ContractEntity existingContract = buildValidContract(contractId);

        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(existingContract));
        given(contractRepository.save(any(ContractEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        contractService.dismissalWork(contractId, dto);

        // then
        ArgumentCaptor<ContractEntity> contractCaptor = ArgumentCaptor.forClass(ContractEntity.class);
        then(contractRepository).should().save(contractCaptor.capture());

        ContractEntity terminatedContract = contractCaptor.getValue();
        assertThat(terminatedContract.getTerminationReason()).isEqualTo(ContractEntity.TerminationReason.DESPIDO);
        assertThat(terminatedContract.getEndDate()).isEqualTo(LocalDate.now());
        assertThat(terminatedContract.getTerminationDescription()).isEqualTo(TERMINATION_DESCRIPTION);
    }

    @Test
    void shouldPropagateExceptionWhenContractNotFound() {
        // given
        Long contractId = INVALID_CONTRACT_ID;
        FinishContractDto dto = buildFinishContractDto();

        given(contractRepository.findById(INVALID_CONTRACT_ID))
                .willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> contractService.dismissalWork(contractId, dto))
                .isInstanceOf(ValueNotFoundException.class);
    }

    @Test
    void shouldUseCurrentDateForTermination() {
        // given
        Long contractId = VALID_CONTRACT_ID;
        FinishContractDto dto = buildFinishContractDto();
        ContractEntity existingContract = buildValidContract(contractId);

        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(existingContract));
        given(contractRepository.save(any(ContractEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        contractService.dismissalWork(contractId, dto);

        // then
        ArgumentCaptor<ContractEntity> contractCaptor = ArgumentCaptor.forClass(ContractEntity.class);
        then(contractRepository).should().save(contractCaptor.capture());

        assertThat(contractCaptor.getValue().getEndDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void shouldThrowRequestConflictExceptionWhenContractStartDateIsInFutureDimisses() {
        // given
        Long contractId = VALID_CONTRACT_ID;
        FinishContractDto dto = buildFinishContractDto();
        ContractEntity futureContract = buildValidContract(contractId)
                .toBuilder()
                .startDate(LocalDate.now().plusDays(1))
                .build();

        given(contractRepository.findById(VALID_CONTRACT_ID))
                .willReturn(Optional.of(futureContract));

        // when / then
        assertThatThrownBy(() -> contractService.dismissalWork(contractId, dto))
                .isInstanceOf(RequestConflictException.class)
                .hasMessage("No se puede despedir al empleado con una fecha anterior al inicio del contrato.");

        then(contractRepository).should().findById(VALID_CONTRACT_ID);
        then(contractRepository).shouldHaveNoMoreInteractions();
    }

    /**
     * tests function findAllContractsOrderedByCreationDate
     */
    @Test
    void shouldReturnContractsOrderedByCreationDateWhenEmployeeHasContracts() {
        // given
        List<ContractDto> expectedContracts = List.of(
                buildContractDtoWithCreatedAt(1L, Instant.parse("2023-01-03T00:00:00Z")),
                buildContractDtoWithCreatedAt(2L, Instant.parse("2023-01-02T00:00:00Z")),
                buildContractDtoWithCreatedAt(3L, Instant.parse("2023-01-01T00:00:00Z"))
        );

        given(contractRepository.findAllByEmployeeIdOrderByCreatedAtDesc(
                eq(EMPLOYEE_WITH_CONTRACTS_ID),
                eq(ContractDto.class)))
                .willReturn(expectedContracts);

        // when
        List<ContractDto> result = contractService.findAllContractsOrderedByCreationDate(EMPLOYEE_WITH_CONTRACTS_ID);

        // then
        assertThat(result).isEqualTo(expectedContracts);

        then(contractRepository).should().findAllByEmployeeIdOrderByCreatedAtDesc(
                EMPLOYEE_WITH_CONTRACTS_ID, ContractDto.class);
    }

    @Test
    void shouldReturnEmptyListWhenEmployeeHasNoContracts() {
        // given
        given(contractRepository.findAllByEmployeeIdOrderByCreatedAtDesc(
                eq(EMPLOYEE_WITHOUT_CONTRACTS_ID),
                eq(ContractDto.class)))
                .willReturn(Collections.emptyList());

        // when
        List<ContractDto> result = contractService.findAllContractsOrderedByCreationDate(EMPLOYEE_WITHOUT_CONTRACTS_ID);

        // then
        then(contractRepository).should().findAllByEmployeeIdOrderByCreatedAtDesc(
                EMPLOYEE_WITHOUT_CONTRACTS_ID, ContractDto.class);
    }

    /**
     * tests function findAllEmployees
     */
    @Test
    void shouldReturnAllEmployeesOrderedByCreationDateWhenNoAreaFilter() {
        // given
        List<EmployeeDto> expectedEmployees = List.of(
                buildEmployeeDto(1L, NAME_EMPLOYEE, VALID_AREA_NAME, EMPLOYEE_CREATED_AT),
                buildEmployeeDto(2L, "Jane Smith", VALID_AREA_NAME, EMPLOYEE_CREATED_AT.minus(1, ChronoUnit.DAYS))
        );

        given(employeeRepository.findAllByOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(expectedEmployees);

        // when
        List<EmployeeDto> result = contractService.findAllEmployees(null);

        // then
        assertThat(result)
                .isEqualTo(expectedEmployees);
        then(employeeRepository).should().findAllByOrderByCreatedAtDesc(EmployeeDto.class);
        then(employeeRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void shouldReturnAllEmployeesOrderedByCreationDateWhenAreaIdIsInvalid() {
        // given
        List<EmployeeDto> expectedEmployees = List.of(
                buildEmployeeDto(1L, NAME_EMPLOYEE, VALID_AREA_NAME, EMPLOYEE_CREATED_AT)
        );

        given(employeeRepository.findAllByOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(expectedEmployees);

        // when
        List<EmployeeDto> result = contractService.findAllEmployees(INVALID_AREA_ID);

        // then
        assertThat(result).isEqualTo(expectedEmployees);
        then(employeeRepository).should().findAllByOrderByCreatedAtDesc(EmployeeDto.class);
    }

    @Test
    void shouldReturnFilteredEmployeesByAreaOrderedByCreationDateWhenValidAreaId() {
        // given
        List<EmployeeDto> expectedEmployees = List.of(
                buildEmployeeDto(1L, NAME_EMPLOYEE, VALID_AREA_NAME, EMPLOYEE_CREATED_AT),
                buildEmployeeDto(3L, "Mike Johnson", VALID_AREA_NAME, EMPLOYEE_CREATED_AT.minus(1, ChronoUnit.HOURS))
        );

        given(employeeRepository.findAllByAreaIdOrderByCreatedAtDesc(eq(VALID_AREA_ID), eq(EmployeeDto.class)))
                .willReturn(expectedEmployees);

        // when
        List<EmployeeDto> result = contractService.findAllEmployees(VALID_AREA_ID);

        // then
        then(employeeRepository).should().findAllByAreaIdOrderByCreatedAtDesc(VALID_AREA_ID, EmployeeDto.class);
        then(employeeRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesFoundForArea() {
        // given
        given(employeeRepository.findAllByAreaIdOrderByCreatedAtDesc(eq(VALID_AREA_ID), eq(EmployeeDto.class)))
                .willReturn(Collections.emptyList());

        // when
        List<EmployeeDto> result = contractService.findAllEmployees(VALID_AREA_ID);

        // then
        assertThat(result.isEmpty()).isTrue();
        then(employeeRepository).should().findAllByAreaIdOrderByCreatedAtDesc(VALID_AREA_ID, EmployeeDto.class);
    }

    /**
     * tests function constructReport
     */
    @Test
    void shouldConstructReportWithEmployeesHavingContracts() {
        // given
        EmployeeDto employeeWithContracts = buildEmployeeDto(1L, NAME_EMPLOYEE, AREA_IT);
        EmployeeDto employeeWithoutContracts = buildEmployeeDto(2L, "Jane Smith", AREA_HR);

        ContractDto activeContract = buildContractDto(1L, 1L, VALID_SALARY, VALID_START_DATE);
        ContractDto historicalContract = buildContractDto(2L, 1L, VALID_SALARY, VALID_START_DATE.minusYears(1));

        List<EmployeeDto> employees = List.of(employeeWithContracts, employeeWithoutContracts);
        List<ContractDto> contracts = List.of(activeContract, historicalContract);

        // when
        ReportEmployeeContracts report = contractService.constructReport(employees, contracts);

        // then

    }

    @Test
    void shouldReturnEmptyReportWhenNoEmployeesHaveContracts() {
        // given
        EmployeeDto employee1 = buildEmployeeDto(1L, NAME_EMPLOYEE, AREA_IT);
        EmployeeDto employee2 = buildEmployeeDto(2L, "Jane Smith", AREA_HR);
        List<EmployeeDto> employees = List.of(employee1, employee2);
        List<ContractDto> contracts = List.of(); // No contracts

        // when
        ReportEmployeeContracts report = contractService.constructReport(employees, contracts);

        // then
        assertThat(report.report().isEmpty()).isTrue();
    }

    @Test
    void shouldIncludeAllContractsForEmployeeInReport() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE, AREA_IT);
        ContractDto contract1 = buildContractDto(1L, 1L, new BigDecimal("5000.00"), VALID_START_DATE);
        ContractDto contract2 = buildContractDto(2L, 1L, new BigDecimal("6000.00"), VALID_START_DATE.plusMonths(6));

        // when
        ReportEmployeeContracts report = contractService.constructReport(
                List.of(employee),
                List.of(contract1, contract2)
        );

        // then

        List<HistoryEmployeeContractsDto> reportList = report.report();

        assertThat(reportList).isNotNull();
        assertThat(reportList.size()).isGreaterThan(0);

    }

    @Test
    void shouldMaintainContractOrderInReport() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE, AREA_IT);
        ContractDto oldestContract = buildContractDto(1L, 1L, VALID_SALARY, VALID_START_DATE.minusYears(1));
        ContractDto newestContract = buildContractDto(2L, 1L, VALID_SALARY, VALID_START_DATE);

        // when
        ReportEmployeeContracts report = contractService.constructReport(
                List.of(employee),
                List.of(newestContract, oldestContract)
        );

        // then
        List<HistoryEmployeeContractsDto> reportList = report.report();

        assertThat(reportList).isNotNull();
        assertThat(reportList.size()).isGreaterThan(0);
    }

    /**
     * tests function reportEmployeeContracts
     */
    @Test
    void shouldGenerateReportWithDateFilterWhenDatesProvided() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE);
        ContractDto contract = buildContractDto(1L, 1L, START_DATE.plusDays(1));

        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(List.of(employee));
        given(contractRepository.findAllContractsBetweenDates(START_DATE, END_DATE))
                .willReturn(List.of(contract));

        // when
        ReportEmployeeContracts report = contractService.reportEmployeeContracts(VALID_AREA_ID, START_DATE, END_DATE);

        // then
        assertNotNull(report);
        assertEquals(1, report.report().size());
        assertEquals(1, report.report().get(0).contracts().size());
        assertEquals(1L, report.report().get(0).id());
        then(contractRepository).should().findAllContractsBetweenDates(START_DATE, END_DATE);
    }

    @Test
    void shouldGenerateReportWithoutDateFilterWhenDatesNotProvided() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE);
        ContractDto contract = buildContractDto(1L, 1L, START_DATE);

        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(List.of(employee));
        given(contractRepository.findAllByOrderByCreatedAtDesc(ContractDto.class))
                .willReturn(List.of(contract));

        // when
        ReportEmployeeContracts report = contractService.reportEmployeeContracts(VALID_AREA_ID, null, null);

        // then
        assertNotNull(report);
        assertEquals(1, report.report().size());
        assertEquals(1, report.report().get(0).contracts().size());
        then(contractRepository).should().findAllByOrderByCreatedAtDesc(ContractDto.class);
    }

    @Test
    void shouldReturnEmptyReportWhenNoEmployeesFound() {
        // given
        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(Collections.emptyList());

        // when
        ReportEmployeeContracts report = contractService.reportEmployeeContracts(VALID_AREA_ID, null, null);

        // then
        assertNotNull(report);
        assertTrue(report.report().isEmpty());
    }

    @Test
    void shouldReturnEmptyReportWhenNoContractsFound() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE);

        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(List.of(employee));
        given(contractRepository.findAllByOrderByCreatedAtDesc(ContractDto.class))
                .willReturn(Collections.emptyList());

        // when
        ReportEmployeeContracts report = contractService.reportEmployeeContracts(VALID_AREA_ID, null, null);

        // then
        assertNotNull(report);
        assertTrue(report.report().isEmpty());
    }

    /**
     * tests function reportTerminatedContracts
     */
    @Test
    void shouldReturnTerminatedContractsReportWithDateFilter() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE);
        ContractDto terminatedContract = buildTerminatedContractDto(1L, 1L, END_DATE.minusDays(10));

        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(List.of(employee));
        given(contractRepository.findTerminatedContractsBetweenDates(START_DATE, END_DATE))
                .willReturn(List.of(terminatedContract));

        // when
        ReportEmployeeContracts report = contractService.reportTerminatedContracts(VALID_AREA_ID, START_DATE, END_DATE);

        // then
        assertNotNull(report);
        assertEquals(1, report.report().size());
        assertEquals(1, report.report().get(0).contracts().size());
        assertNotNull(report.report().get(0).contracts().get(0).endDate());
        then(contractRepository).should().findTerminatedContractsBetweenDates(START_DATE, END_DATE);
    }

    @Test
    void shouldReturnTerminatedContractsReportWithoutDateFilter() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE);
        ContractDto terminatedContract = buildTerminatedContractDto(1L, 1L, END_DATE.minusDays(5));

        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(List.of(employee));
        given(contractRepository.findTerminatedContracts())
                .willReturn(List.of(terminatedContract));

        // when
        ReportEmployeeContracts report = contractService.reportTerminatedContracts(VALID_AREA_ID, null, null);

        // then
        assertNotNull(report);
        assertEquals(1, report.report().size());
        assertEquals(1, report.report().get(0).contracts().size());
        assertNotNull(report.report().get(0).contracts().get(0).endDate());
        then(contractRepository).should().findTerminatedContracts();
    }

    @Test
    void shouldReturnEmptyReportWhenNoTerminatedContractsFound() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE);

        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(List.of(employee));
        given(contractRepository.findTerminatedContracts())
                .willReturn(Collections.emptyList());

        // when
        ReportEmployeeContracts report = contractService.reportTerminatedContracts(VALID_AREA_ID, null, null);

        // then
        assertNotNull(report);
        assertTrue(report.report().isEmpty());
    }

    @Test
    void shouldFilterOnlyTerminatedContracts() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE);
        ContractDto terminatedContract = buildTerminatedContractDto(1L, 1L, END_DATE.minusDays(1));
        ContractDto activeContract = buildContractDto(2L, 1L, START_DATE); // No end date = active

        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(List.of(employee));
        given(contractRepository.findTerminatedContracts())
                .willReturn(List.of(terminatedContract, activeContract));

        // when
        ReportEmployeeContracts report = contractService.reportTerminatedContracts(VALID_AREA_ID, null, null);

        // then
        assertEquals(1, report.report().size());
        assertNotNull(report.report().get(0).contracts().get(0).endDate());
    }

    @Test
    void shouldUseFindAllContractsWhenDatesAreNull() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE);
        ContractDto contract = buildContractDto(1L, 1L, LocalDate.now());

        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(List.of(employee));
        given(contractRepository.findAllByOrderByCreatedAtDesc(ContractDto.class))
                .willReturn(List.of(contract));

        // when
        ReportEmployeeContracts report = contractService.reportEmployeeContracts(VALID_AREA_ID, START_DATE, null);

        // then
        then(contractRepository).should().findAllByOrderByCreatedAtDesc(ContractDto.class);
        then(contractRepository).should(never()).findAllContractsBetweenDates(any(), any());
    }

    @Test
    void shouldUseFindAllBetweenDatesWhenDatesAreProvided() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE);
        ContractDto contract = buildContractDto(1L, 1L, LocalDate.now());

        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(List.of(employee));
        given(contractRepository.findAllByOrderByCreatedAtDesc(ContractDto.class))
                .willReturn(List.of(contract));

        // when
        ReportEmployeeContracts report = contractService.reportEmployeeContracts(VALID_AREA_ID, null, END_DATE);

        // then
        then(contractRepository).should().findAllByOrderByCreatedAtDesc(ContractDto.class);
        then(contractRepository).should(never()).findAllContractsBetweenDates(any(), any());
    }

    @Test
    void shouldUseFindTerminatedContractsWhenDatesAreNull() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE);
        ContractDto terminatedContract = buildTerminatedContractDto(1L, 1L, END_DATE.minusDays(1));

        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(List.of(employee));
        given(contractRepository.findTerminatedContracts())
                .willReturn(List.of(terminatedContract));

        // when
        ReportEmployeeContracts report = contractService.reportTerminatedContracts(VALID_AREA_ID, START_DATE, null);

        // then
        then(contractRepository).should().findTerminatedContracts();
        then(contractRepository).should(never()).findTerminatedContractsBetweenDates(any(), any());
    }

    @Test
    void shouldUseFindTerminatedBetweenDatesWhenDatesAreProvided() {
        // given
        EmployeeDto employee = buildEmployeeDto(1L, NAME_EMPLOYEE);
        ContractDto terminatedContract = buildTerminatedContractDto(1L, 1L, END_DATE.minusDays(1));

        given(contractService.findAllEmployees(VALID_AREA_ID))
                .willReturn(List.of(employee));
        given(contractRepository.findTerminatedContracts())
                .willReturn(List.of(terminatedContract));

        // when
        ReportEmployeeContracts report = contractService.reportTerminatedContracts(VALID_AREA_ID, null, END_DATE);

        // then
        then(contractRepository).should().findTerminatedContracts();
        then(contractRepository).should(never()).findTerminatedContractsBetweenDates(any(), any());
    }

    // metodos extras como utils
    private ContractEntity buildValidContract() {
        return ContractEntity.builder()
                .id(VALID_CONTRACT_ID)
                .salary(new BigDecimal("5000.00"))
                .startDate(LocalDate.of(2023, 1, 1))
                .employee(new EmployeeEntity())
                .build();
    }

    private FinishContract buildFinishContractRequest(Long contractId) {
        return FinishContract
                .builder()
                .idContract(contractId)
                .date(FINISH_DATE)
                .terminationReason(ContractEntity.TerminationReason.FIN_CONTRATO)
                .description(TERMINATION_DESCRIPTION)
                .build();
    }

    private CreateEmployeeDto buildValidCreateEmployeeDto() {
        return CreateEmployeeDto.builder()
                .fullName(NAME_EMPLOYEE)
                .cui(EMPLOYEE_CUI)
                .phone("12345678")
                .email(EMPLOYEE_EMAIL)
                .area(1L)
                .isSpecialist(false)
                .startDate(VALID_START_DATE)
                .salary(VALID_SALARY)
                .igssDiscount(VALID_IGSS_DISCOUNT)
                .irtraDiscount(VALID_IRTRA_DISCOUNT)
                .build();
    }

    private EmployeeEntity buildValidEmployee() {
        return EmployeeEntity.builder()
                .id(1L)
                .fullName(NAME_EMPLOYEE)
                .cui(EMPLOYEE_CUI)
                .email(EMPLOYEE_EMAIL)
                .area(AreaEntity.builder().id(1L).name("Enferemeria").build())
                .build();
    }

    private ContractEntity buildExpectedContract(CreateEmployeeDto dto, EmployeeEntity employee) {
        return ContractEntity.builder()
                .salary(dto.salary())
                .igssDiscount(dto.igssDiscount())
                .irtraDiscount(dto.irtraDiscount())
                .startDate(dto.startDate())
                .employee(employee)
                .build();
    }

    private ContractDto buildValidContractDto(Long employeeId) {
        return ContractDto.builder()
                .id(1L)
                .employeeId(employeeId)
                .salary(VALID_SALARY)
                .igssDiscount(VALID_IGSS_DISCOUNT)
                .irtraDiscount(VALID_IRTRA_DISCOUNT)
                .startDate(VALID_START_DATE)
                .createdAt(CONTRACT_CREATED_AT)
                .updatedAt(CONTRACT_CREATED_AT)
                .build();
    }

    private NewContractDto buildValidNewContractDto() {
        return NewContractDto.builder()
                .idContract(VALID_CONTRACT_ID)
                .idEmployee(VALID_EMPLOYEE_ID)
                .salary(NEW_SALARY)
                .igssDiscount(NEW_IGSS_DISCOUNT)
                .irtraDiscount(NEW_IRTRA_DISCOUNT)
                .build();
    }

    private ContractEntity buildValidContract(Long contractId) {
        return ContractEntity.builder()
                .id(contractId)
                .salary(VALID_SALARY)
                .startDate(VALID_START_DATE)
                .employee(buildValidEmployee())
                .build();
    }

    private FinishContractDto buildFinishContractDto() {
        return new FinishContractDto(TERMINATION_DESCRIPTION);
    }

    private UpdateSalaryDto buildUpdateSalaryDto(boolean isIncrement) {
        return UpdateSalaryDto.builder()
                .salary(UPDATED_SALARY)
                .isIncrement(isIncrement)
                .build();
    }

    private ContractDto buildContractDtoWithCreatedAt(Long contractId, Instant createdAt) {
        return ContractDto.builder()
                .id(contractId)
                .employeeId(EMPLOYEE_WITH_CONTRACTS_ID)
                .salary(VALID_SALARY)
                .startDate(VALID_START_DATE)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }

    private EmployeeDto buildEmployeeDto(Long id, String fullName, String areaName, Instant createdAt) {
        return EmployeeDto.builder()
                .id(id)
                .fullName(fullName)
                .email(fullName.toLowerCase().replace(" ", ".") + "@company.com")
                .areaName(areaName)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }

    private EmployeeDto buildEmployeeDto(Long id, String fullName) {
        return EmployeeDto.builder()
                .id(id)
                .fullName(fullName)
                .cui("1234567890123")
                .email(fullName.toLowerCase().replace(" ", ".") + "@company.com")
                .areaName(AREA_NAME)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private ContractDto buildContractDto(Long id, Long employeeId, LocalDate startDate) {
        return ContractDto.builder()
                .id(id)
                .employeeId(employeeId)
                .salary(new BigDecimal("5000.00"))
                .startDate(startDate)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private EmployeeDto buildEmployeeDto(Long id, String fullName, String areaName) {
        return EmployeeDto.builder()
                .id(id)
                .fullName(fullName)
                .cui(VALID_CUI)
                .email(fullName.toLowerCase().replace(" ", ".") + "@company.com")
                .areaName(areaName)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private ContractDto buildContractDto(Long id, Long employeeId, BigDecimal salary, LocalDate startDate) {
        return ContractDto.builder()
                .id(id)
                .employeeId(employeeId)
                .salary(salary)
                .startDate(startDate)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private ContractDto buildTerminatedContractDto(Long id, Long employeeId, LocalDate endDate) {
        return ContractDto.builder()
                .id(id)
                .employeeId(employeeId)
                .salary(new BigDecimal("5000.00"))
                .startDate(START_DATE)
                .endDate(endDate)
                .terminationReason(ContractEntity.TerminationReason.FIN_CONTRATO)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

}