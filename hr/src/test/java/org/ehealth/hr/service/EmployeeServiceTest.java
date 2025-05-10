package org.ehealth.hr.service;

import feign.FeignException;
import org.ehealth.hr.client.PatientClient;
import org.ehealth.hr.domain.dto.ContractDto;
import org.ehealth.hr.domain.dto.CreateEmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeResponseDto;
import org.ehealth.hr.domain.dto.reports.AssignedEmployeeReportDto;
import org.ehealth.hr.domain.dto.reports.ReportAssignedEmployeeDto;
import org.ehealth.hr.domain.entity.AreaEntity;
import org.ehealth.hr.domain.entity.ContractEntity;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.exception.RequestConflictException;
import org.ehealth.hr.domain.exception.ValueNotFoundException;
import org.ehealth.hr.repository.AreaRepository;
import org.ehealth.hr.repository.ContractRepository;
import org.ehealth.hr.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    private static final String VALID_CUI = "1234567890123";
    private static final String VALID_EMAIL = "valid@email.com";
    private static final String VALID_FULL_NAME = "John Doe";
    private static final String VALID_PHONE = "123456789";
    private static final Long VALID_AREA_ID = 1L;
    private static final LocalDate TEST_START_DATE = LocalDate.of(2023, 12, 31);

    private static final String EXISTING_CUI = "9876543210987";
    private static final String NON_EXISTING_CUI = "0000000000000";
    private static final String AREA_NAME = "Test Area";

    private static final Instant EARLIER_DATE = Instant.parse("2023-01-01T00:00:00Z");
    private static final Instant LATER_DATE = Instant.parse("2023-01-02T00:00:00Z");

    private static final String MEDICOS_AREA = "Medicos";
    private static final String VALID_START_DATE = "2023-01-01";
    private static final String VALID_END_DATE = "2023-12-31";
    private static final LocalDate TEST_ADMISSION_DATE = LocalDate.of(2023, 6, 1);
    private static final LocalDate TEST_DISCHARGE_DATE = LocalDate.of(2023, 6, 15);

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private IContractService contractService;

    @Mock
    private IVacationService vacationService;

    @Mock
    private PatientClient patientClient;

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {

    }

    /**
     * tests function createEmployee
     */
    @Test
    void shouldCreateEmployeeSuccessfullyWhenNotSpecialist() {
        // given
        CreateEmployeeDto request = buildCreateEmployeeDto(false);
        AreaEntity area = buildAreaEntity();
        EmployeeEntity savedEmployee = buildEmployeeEntity(false, area);

        given(employeeRepository.existsByEmail(VALID_EMAIL)).willReturn(false);
        given(employeeRepository.existsByCui(VALID_CUI)).willReturn(false);
        given(areaRepository.findById(VALID_AREA_ID)).willReturn(Optional.of(area));
        given(employeeRepository.save(any(EmployeeEntity.class))).willReturn(savedEmployee);

        // when
        EmployeeResponseDto result = employeeService.createEmployee(request);

        // then
        assertNotNull(result);
        assertEquals(VALID_FULL_NAME, result.fullName());
        assertEquals(VALID_CUI, result.cui());
        assertEquals(VALID_EMAIL, result.email());
        assertFalse(result.isSpecialist());

        // verify interactions
        verify(contractService).createContractWithEmployee(request, savedEmployee);
    }

    @Test
    void shouldCreateEmployeeSuccessfullyWhenSpecialist() {
        // given
        CreateEmployeeDto request = buildCreateEmployeeDto(true);
        AreaEntity area = buildAreaEntity();
        EmployeeEntity savedEmployee = buildEmployeeEntity(true, area);

        given(employeeRepository.existsByEmail(VALID_EMAIL)).willReturn(false);
        given(employeeRepository.existsByCui(VALID_CUI)).willReturn(false);
        given(areaRepository.findById(VALID_AREA_ID)).willReturn(Optional.of(area));
        given(employeeRepository.save(any(EmployeeEntity.class))).willReturn(savedEmployee);

        // when
        EmployeeResponseDto result = employeeService.createEmployee(request);

        // then
        assertNotNull(result);
        assertEquals(VALID_FULL_NAME, result.fullName());
        assertEquals(VALID_CUI, result.cui());
        assertEquals(VALID_EMAIL, result.email());
        assertTrue(result.isSpecialist());

        // verify no contract or vacation creation for specialists
        verify(contractService, never()).createContractWithEmployee(any(), any());
        verify(vacationService, never()).createVacationWithEmployee(any(), any());
    }

    @Test
    void shouldThrowConflictExceptionWhenEmailAlreadyExists() {
        // given
        CreateEmployeeDto request = buildCreateEmployeeDto(false);

        given(employeeRepository.existsByEmail(VALID_EMAIL)).willReturn(true);

        // when / then
        RequestConflictException exception = assertThrows(RequestConflictException.class,
                () -> employeeService.createEmployee(request));

        assertEquals("Ya existe un empleado con el CUI o correo electrónico proporcionado.", exception.getMessage());

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void shouldThrowConflictExceptionWhenCuiAlreadyExists() {
        // given
        CreateEmployeeDto request = buildCreateEmployeeDto(false);

        given(employeeRepository.existsByEmail(VALID_EMAIL)).willReturn(false);
        given(employeeRepository.existsByCui(VALID_CUI)).willReturn(true);

        // when / then
        RequestConflictException exception = assertThrows(RequestConflictException.class,
                () -> employeeService.createEmployee(request));

        assertEquals("Ya existe un empleado con el CUI o correo electrónico proporcionado.", exception.getMessage());

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void shouldThrowConflictExceptionWhenAreaNotFound() {
        // given
        CreateEmployeeDto request = buildCreateEmployeeDto(false);

        given(employeeRepository.existsByEmail(VALID_EMAIL)).willReturn(false);
        given(employeeRepository.existsByCui(VALID_CUI)).willReturn(false);
        given(areaRepository.findById(VALID_AREA_ID)).willReturn(Optional.empty());

        // when / then
        RequestConflictException exception = assertThrows(RequestConflictException.class,
                () -> employeeService.createEmployee(request));

        assertEquals("Área no encontrada con ID: " + VALID_AREA_ID, exception.getMessage());

        verify(employeeRepository, never()).save(any());
    }

    /**
     * tests function findEmployeeByCui
     */
    @Test
    void shouldReturnEmployeeDtoWhenCuiExists() {
        // given
        EmployeeDto expectedDto = buildEmployeeDto();
        given(employeeRepository.findByCui(EXISTING_CUI, EmployeeDto.class))
                .willReturn(Optional.of(expectedDto));

        // when
        EmployeeDto result = employeeService.findEmployeeByCui(EXISTING_CUI);

        // then
        assertNotNull(result);
        assertEquals(EXISTING_CUI, result.cui());
        assertEquals(VALID_FULL_NAME, result.fullName());
        assertEquals(AREA_NAME, result.areaName());
        verify(employeeRepository).findByCui(EXISTING_CUI, EmployeeDto.class);
    }

    @Test
    void shouldThrowValueNotFoundExceptionWhenCuiNotExists() {
        // given
        given(employeeRepository.findByCui(NON_EXISTING_CUI, EmployeeDto.class))
                .willReturn(Optional.empty());

        // when / then
        ValueNotFoundException exception = assertThrows(ValueNotFoundException.class,
                () -> employeeService.findEmployeeByCui(NON_EXISTING_CUI));

        assertEquals("El empleado que se intenta buscar no existe", exception.getMessage());
        verify(employeeRepository).findByCui(NON_EXISTING_CUI, EmployeeDto.class);
    }

    /**
     * tests function findAllEmployeesOrdered
     */
    @Test
    void shouldReturnEmptyListWhenNoEmployeesExist() {
        // given
        given(employeeRepository.findAllByOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(Collections.emptyList());

        // when
        List<EmployeeDto> result = employeeService.findAllEmployeesOrdered();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository).findAllByOrderByCreatedAtDesc(EmployeeDto.class);
    }

    @Test
    void shouldReturnEmployeesOrderedByCreatedAtDesc() {
        // given
        List<EmployeeDto> mockEmployees = Arrays.asList(
                buildEmployeeDtoWithDate(1L, LATER_DATE),
                buildEmployeeDtoWithDate(2L, EARLIER_DATE)
        );

        given(employeeRepository.findAllByOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(mockEmployees);

        // when
        List<EmployeeDto> result = employeeService.findAllEmployeesOrdered();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(LATER_DATE, result.get(0).createdAt());
        assertEquals(EARLIER_DATE, result.get(1).createdAt());
        verify(employeeRepository).findAllByOrderByCreatedAtDesc(EmployeeDto.class);
    }

    @Test
    void shouldReturnAllEmployeesWithCorrectData() {
        // given
        List<EmployeeDto> mockEmployees = Collections.singletonList(
                buildEmployeeDto()
        );

        given(employeeRepository.findAllByOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(mockEmployees);

        // when
        List<EmployeeDto> result = employeeService.findAllEmployeesOrdered();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());

        EmployeeDto employee = result.get(0);
        assertEquals(VALID_FULL_NAME, employee.fullName());
        assertEquals(EXISTING_CUI, employee.cui());
        assertEquals(AREA_NAME, employee.areaName());
    }

    /**
     * tests function findEmployeesByArea
     */
    @Test
    void shouldReturnEmployeesByAreaWhenAreaHasEmployees() {
        // given
        Long validAreaId = VALID_AREA_ID;
        List<EmployeeDto> mockEmployees = Arrays.asList(
                buildEmployeeDtoForArea(validAreaId, "Area 1", LATER_DATE),
                buildEmployeeDtoForArea(validAreaId, "Area 1", EARLIER_DATE)
        );

        given(employeeRepository.findAllByAreaIdOrderByCreatedAtDesc(validAreaId, EmployeeDto.class))
                .willReturn(mockEmployees);

        // when
        List<EmployeeDto> result = employeeService.findEmployeesByArea(validAreaId);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(validAreaId, result.get(0).id()); // Asumiendo que id en Dto es el del empleado
        assertEquals("Area 1", result.get(0).areaName());
        assertEquals(LATER_DATE, result.get(0).createdAt());
        verify(employeeRepository).findAllByAreaIdOrderByCreatedAtDesc(validAreaId, EmployeeDto.class);
    }

    @Test
    void shouldReturnEmptyListWhenAreaHasNoEmployees() {
        // given
        Long validAreaId = VALID_AREA_ID;
        given(employeeRepository.findAllByAreaIdOrderByCreatedAtDesc(validAreaId, EmployeeDto.class))
                .willReturn(Collections.emptyList());

        // when
        List<EmployeeDto> result = employeeService.findEmployeesByArea(validAreaId);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository).findAllByAreaIdOrderByCreatedAtDesc(validAreaId, EmployeeDto.class);
    }

    /**
     *  tests function getReportAssignedEmployeeInRange
     */
    @Test
    void shouldReturnReportWithOngoingAssignmentsWhenFilterIs1() {
        // given
        int filter = 1; // Solo admisiones vigentes
        EmployeeDto medicEmployee = buildMedicEmployeeDto(1L);
        AssignedEmployeeReportDto ongoingAssignment = buildAssignment(1L, TEST_ADMISSION_DATE, null); // Sin fecha de alta

        given(employeeRepository.findAllByIsSpecialistFalseOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(List.of(medicEmployee));
        given(patientClient.getDoctorsAssignedReport(VALID_START_DATE, VALID_END_DATE))
                .willReturn(List.of(ongoingAssignment));
        given(contractRepository.findFirstByEmployeeIdOrderByCreatedAtDesc(1L, ContractDto.class))
                .willReturn(Optional.of(buildContractDto()));

        // when
        ReportAssignedEmployeeDto result = employeeService.getReportAssignedEmployeeInRange(
                filter, VALID_START_DATE, VALID_END_DATE);

        // then
        assertNotNull(result);
        assertEquals(1, result.report().size());
        assertEquals(1, result.report().get(0).assignedList().size());
        assertNull(result.report().get(0).assignedList().get(0).dischargeDate());
    }

    @Test
    void shouldReturnReportWithFinishedAssignmentsWhenFilterIs2() {
        // given
        int filter = 2; // Solo admisiones terminadas
        EmployeeDto medicEmployee = buildMedicEmployeeDto(1L);
        AssignedEmployeeReportDto finishedAssignment = buildAssignment(1L, TEST_ADMISSION_DATE, TEST_DISCHARGE_DATE);

        given(employeeRepository.findAllByIsSpecialistFalseOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(List.of(medicEmployee));
        given(patientClient.getDoctorsAssignedReport(VALID_START_DATE, VALID_END_DATE))
                .willReturn(List.of(finishedAssignment));
        given(contractRepository.findFirstByEmployeeIdOrderByCreatedAtDesc(1L, ContractDto.class))
                .willReturn(Optional.of(buildContractDto()));

        // when
        ReportAssignedEmployeeDto result = employeeService.getReportAssignedEmployeeInRange(
                filter, VALID_START_DATE, VALID_END_DATE);

        // then
        assertNotNull(result);
        assertEquals(1, result.report().size());
        assertEquals(1, result.report().get(0).assignedList().size());
        assertNotNull(result.report().get(0).assignedList().get(0).dischargeDate());
    }

    @Test
    void shouldReturnAllAssignmentsWhenFilterIsNot1Or2() {
        // given
        int filter = 0; // Todos los casos
        EmployeeDto medicEmployee = buildMedicEmployeeDto(1L);
        AssignedEmployeeReportDto ongoingAssignment = buildAssignment(1L, TEST_ADMISSION_DATE, null);
        AssignedEmployeeReportDto finishedAssignment = buildAssignment(1L, TEST_ADMISSION_DATE, TEST_DISCHARGE_DATE);

        given(employeeRepository.findAllByIsSpecialistFalseOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(List.of(medicEmployee));
        given(patientClient.getDoctorsAssignedReport(VALID_START_DATE, VALID_END_DATE))
                .willReturn(List.of(ongoingAssignment, finishedAssignment));
        given(contractRepository.findFirstByEmployeeIdOrderByCreatedAtDesc(1L, ContractDto.class))
                .willReturn(Optional.of(buildContractDto()));

        // when
        ReportAssignedEmployeeDto result = employeeService.getReportAssignedEmployeeInRange(
                filter, VALID_START_DATE, VALID_END_DATE);

        // then
        assertNotNull(result);
        assertEquals(1, result.report().size());
        assertEquals(2, result.report().get(0).assignedList().size());
    }

    @Test
    void shouldThrowConflictExceptionWhenFeignClientFails() {
        // given
        given(employeeRepository.findAllByIsSpecialistFalseOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(List.of(buildMedicEmployeeDto(1L)));
        given(patientClient.getDoctorsAssignedReport(VALID_START_DATE, VALID_END_DATE))
                .willThrow(FeignException.class);

        // when / then
        assertThrows(RequestConflictException.class, () ->
                employeeService.getReportAssignedEmployeeInRange(0, VALID_START_DATE, VALID_END_DATE));
    }

    @Test
    void shouldFilterNonMedicEmployees() {
        // given
        EmployeeDto medicEmployee = buildMedicEmployeeDto(1L);
        EmployeeDto nonMedicEmployee = buildEmployeeDtoForArea(2L, "Enfermería", Instant.now());

        given(employeeRepository.findAllByIsSpecialistFalseOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(List.of(medicEmployee, nonMedicEmployee));
        given(patientClient.getDoctorsAssignedReport(VALID_START_DATE, VALID_END_DATE))
                .willReturn(Collections.emptyList());

        // when
        ReportAssignedEmployeeDto result = employeeService.getReportAssignedEmployeeInRange(
                0, VALID_START_DATE, VALID_END_DATE);

        // then
        assertNotNull(result);
        assertTrue(result.report().isEmpty());
    }

    // Métodos de utilidad adicionales
    private EmployeeDto buildMedicEmployeeDto(Long id) {
        return EmployeeDto.builder()
                .id(id)
                .fullName("Dr. " + id)
                .cui("MED" + id)
                .email("medic" + id + "@hospital.com")
                .isSpecialist(false)
                .areaName(MEDICOS_AREA)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private AssignedEmployeeReportDto buildAssignment(Long employeeId, LocalDate admissionDate, LocalDate dischargeDate) {
        return AssignedEmployeeReportDto.builder()
                .employeeId(employeeId)
                .admissionDate(admissionDate)
                .dischargeDate(dischargeDate)
                .fullName("Patient Name")
                .cui("PATIENT" + employeeId)
                .build();
    }

    private ContractDto buildContractDto() {
        return ContractDto.builder()
                .id(1L)
                .terminationReason(ContractEntity.TerminationReason.FIN_CONTRATO)
                .startDate(LocalDate.now())
                .build();
    }

    private CreateEmployeeDto buildCreateEmployeeDto(boolean isSpecialist) {
        return CreateEmployeeDto
                .builder()
                .area(VALID_AREA_ID)
                .cui(VALID_CUI)
                .phone(VALID_PHONE)
                .email(VALID_EMAIL)
                .fullName(VALID_FULL_NAME)
                .isSpecialist(isSpecialist)
                .startDate(TEST_START_DATE)
                .build();
    }

    private AreaEntity buildAreaEntity() {
        return AreaEntity.builder()
                .id(VALID_AREA_ID)
                .name("Test Area")
                .build();
    }

    private EmployeeEntity buildEmployeeEntity(boolean isSpecialist, AreaEntity area) {
        return EmployeeEntity.builder()
                .id(1L)
                .fullName(VALID_FULL_NAME)
                .cui(VALID_CUI)
                .phone(VALID_PHONE)
                .email(VALID_EMAIL)
                .isSpecialist(isSpecialist)
                .area(area)
                .createdAt(Instant.now())
                .build();
    }

    private EmployeeDto buildEmployeeDto() {
        return EmployeeDto.builder()
                .id(1L)
                .fullName(VALID_FULL_NAME)
                .cui(EXISTING_CUI)
                .phone(VALID_PHONE)
                .email(VALID_EMAIL)
                .isSpecialist(false)
                .areaName(AREA_NAME)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private EmployeeDto buildEmployeeDtoWithDate(Long id, Instant createdAt) {
        return EmployeeDto.builder()
                .id(id)
                .fullName(VALID_FULL_NAME + " " + id)
                .cui(EXISTING_CUI + id)
                .phone(VALID_PHONE)
                .email("user" + id + "@email.com")
                .isSpecialist(false)
                .areaName(AREA_NAME)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }

    private EmployeeDto buildEmployeeDtoForArea(Long id, String areaName, Instant createdAt) {
        return EmployeeDto.builder()
                .id(id)
                .fullName("Employee " + id)
                .cui("CUI" + id)
                .phone("Phone" + id)
                .email("employee" + id + "@test.com")
                .isSpecialist(false)
                .areaName(areaName)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }


}