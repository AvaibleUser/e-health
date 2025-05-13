package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.vacation.UpdateRequestVacationDto;
import org.ehealth.hr.domain.dto.vacation.VacationPendingDto;
import org.ehealth.hr.domain.entity.AreaEntity;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.entity.VacationEntity;
import org.ehealth.hr.domain.exception.ValueNotFoundException;
import org.ehealth.hr.repository.EmployeeRepository;
import org.ehealth.hr.repository.VacationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class VacationServiceTest {

    @Mock
    private VacationRepository vacationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private VacationService vacationService;

    private static final LocalDate MONDAY = LocalDate.of(2023, 6, 5); // 5/6/2023 es lunes
    private static final LocalDate FRIDAY = LocalDate.of(2023, 6, 9); // 9/6/2023 es viernes
    private static final LocalDate SATURDAY = LocalDate.of(2023, 6, 10); // 10/6/2023 es sábado
    private static final LocalDate SUNDAY = LocalDate.of(2023, 6, 11); // 11/6/2023 es domingo

    private static final LocalDate NEXT_YEAR_MONDAY = MONDAY.plusYears(1);
    private static final LocalDate EXPECTED_END_DATE = NEXT_YEAR_MONDAY.plusDays(21);

    private static final Long VALID_VACATION_ID = 1L;
    private static final Long INVALID_VACATION_ID = 999L;
    private static final UpdateRequestVacationDto APPROVE_REQUEST =
            new UpdateRequestVacationDto(true);
    private static final UpdateRequestVacationDto REJECT_REQUEST =
            new UpdateRequestVacationDto(false);

    private static final VacationPendingDto PENDING_VACATION_1 = builderVaction();
    private static final VacationPendingDto PENDING_VACATION_2 = VacationPendingDto.builder()
            .employeeId(2L)
            .fullName("Jane Smith")
            .cui("9876543210987")
            .name("HR Department")
            .id(2L)
            .requestedDate(FRIDAY)
            .startDate(FRIDAY.plusYears(1))
            .endDate(EXPECTED_END_DATE)
            .approved(false)
            .state(VacationEntity.State.PENDIENTE)
            .build();

    private static final VacationPendingDto APPROVED_VACATION_1 = VacationPendingDto.builder()
            .employeeId(1L)
            .fullName("John Doe")
            .cui("1234567890123")
            .name("IT Department")
            .id(1L)
            .requestedDate(MONDAY.minusDays(30))
            .startDate(NEXT_YEAR_MONDAY.minusDays(30))
            .endDate(EXPECTED_END_DATE.minusDays(30))
            .approved(true)
            .state(VacationEntity.State.APROVADA)
            .build();

    private static final VacationPendingDto APPROVED_VACATION_2 = VacationPendingDto.builder()
            .employeeId(2L)
            .fullName("Jane Smith")
            .cui("9876543210987")
            .name("HR Department")
            .id(2L)
            .requestedDate(FRIDAY.minusDays(60))
            .startDate(FRIDAY.plusYears(1).minusDays(60))
            .endDate(EXPECTED_END_DATE)
            .approved(true)
            .state(VacationEntity.State.APROVADA)
            .build();

    @BeforeEach
    void setUp() {

    }

    /**
     * tests function addBusinessDays
     */
    @Test
    void shouldAddBusinessDaysWhenStartingOnWeekday() {
        // given
        LocalDate startDate = MONDAY;
        int businessDaysToAdd = 3;

        // when
        LocalDate result = vacationService.addBusinessDays(startDate, businessDaysToAdd);

        // then
        LocalDate expectedDate = LocalDate.of(2023, 6, 8); // Jueves 8/6/2023
        assertEquals(expectedDate, result);
    }

    @Test
    void shouldAddBusinessDaysWhenStartingOnFriday() {
        // given
        LocalDate startDate = FRIDAY;
        int businessDaysToAdd = 2;

        // when
        LocalDate result = vacationService.addBusinessDays(startDate, businessDaysToAdd);

        // then
        LocalDate expectedDate = LocalDate.of(2023, 6, 13); // Martes 13/6/2023 (salta fin de semana)
        assertEquals(expectedDate, result);
    }

    @Test
    void shouldAddBusinessDaysWhenStartingOnSaturday() {
        // given
        LocalDate startDate = SATURDAY;
        int businessDaysToAdd = 2;

        // when
        LocalDate result = vacationService.addBusinessDays(startDate, businessDaysToAdd);

        // then
        LocalDate expectedDate = LocalDate.of(2023, 6, 13); // Martes 13/6/2023 (comienza a contar desde el lunes)
        assertEquals(expectedDate, result);
    }

    @Test
    void shouldAddBusinessDaysWhenStartingOnSunday() {
        // given
        LocalDate startDate = SUNDAY;
        int businessDaysToAdd = 1;

        // when
        LocalDate result = vacationService.addBusinessDays(startDate, businessDaysToAdd);

        // then
        LocalDate expectedDate = LocalDate.of(2023, 6, 12); // Lunes 12/6/2023 (comienza a contar desde el lunes)
        assertEquals(expectedDate, result);
    }

    @Test
    void shouldReturnSameDateWhenAddingZeroBusinessDays() {
        // given
        LocalDate startDate = MONDAY;
        int businessDaysToAdd = 0;

        // when
        LocalDate result = vacationService.addBusinessDays(startDate, businessDaysToAdd);

        // then
        assertEquals(startDate, result);
    }

    @Test
    void shouldSkipWeekendsWhenAddingBusinessDays() {
        // given
        LocalDate startDate = FRIDAY; // Viernes
        int businessDaysToAdd = 1;

        // when
        LocalDate result = vacationService.addBusinessDays(startDate, businessDaysToAdd);

        // then
        LocalDate expectedDate = LocalDate.of(2023, 6, 12); // Lunes siguiente
        assertEquals(expectedDate, result);
    }

    @Test
    void shouldHandleAddingBusinessDaysAcrossMultipleWeeks() {
        // given
        LocalDate startDate = MONDAY;
        int businessDaysToAdd = 10; // Dos semanas completas de lunes a viernes

        // when
        LocalDate result = vacationService.addBusinessDays(startDate, businessDaysToAdd);

        // then
        LocalDate expectedDate = LocalDate.of(2023, 6, 19); // Lunes de la tercera semana
        assertEquals(expectedDate, result);
    }

    /**
     * tests function createVacationWithEmployee
     */
    @Test
    void shouldCreateVacationWithCorrectDatesWhenEmployeeIsValid() {
        // given
        EmployeeEntity validEmployee = buildValidEmployee();
        VacationEntity expectedVacation = VacationEntity.builder()
                .requestedDate(MONDAY)
                .startDate(NEXT_YEAR_MONDAY)
                .endDate(EXPECTED_END_DATE)
                .approved(false)
                .employee(validEmployee)
                .state(VacationEntity.State.PENDIENTE)
                .build();

        given(vacationRepository.save(any(VacationEntity.class))).willReturn(expectedVacation);

        // when
        vacationService.createVacationWithEmployee(validEmployee, MONDAY);

        // then
        ArgumentCaptor<VacationEntity> vacationCaptor = ArgumentCaptor.forClass(VacationEntity.class);
        verify(vacationRepository).save(vacationCaptor.capture());

        VacationEntity savedVacation = vacationCaptor.getValue();
        assertAll(
                () -> assertEquals(MONDAY, savedVacation.getRequestedDate()),
                () -> assertEquals(NEXT_YEAR_MONDAY, savedVacation.getStartDate()),
                () -> assertEquals(EXPECTED_END_DATE, savedVacation.getEndDate()),
                () -> assertEquals(VacationEntity.State.PENDIENTE, savedVacation.getState()),
                () -> assertEquals(validEmployee, savedVacation.getEmployee())
        );
    }

    @Test
    void shouldSetVacationStartDateOneYearAfterRequestedDate() {
        // given
        EmployeeEntity validEmployee = buildValidEmployee();
        LocalDate testDate = LocalDate.of(2023, 12, 15);
        LocalDate expectedStartDate = testDate.plusYears(1);

        // when
        vacationService.createVacationWithEmployee(validEmployee, testDate);

        // then
        ArgumentCaptor<VacationEntity> vacationCaptor = ArgumentCaptor.forClass(VacationEntity.class);
        verify(vacationRepository).save(vacationCaptor.capture());

        assertEquals(expectedStartDate, vacationCaptor.getValue().getStartDate());
    }

    @Test
    void shouldCalculateEndDateAs15BusinessDaysAfterStartDate() {
        // given
        EmployeeEntity validEmployee = buildValidEmployee();
        LocalDate testDate = FRIDAY; // Viernes 9/6/2023
        LocalDate expectedStartDate = testDate.plusYears(1);
        // 15 días laborales desde el start date (considerando fines de semana)
        LocalDate expectedEndDate = expectedStartDate.plusDays(19); // 15 días laborales = 19 días naturales (3 fines de semana)

        // when
        vacationService.createVacationWithEmployee(validEmployee, testDate);

        // then
        ArgumentCaptor<VacationEntity> vacationCaptor = ArgumentCaptor.forClass(VacationEntity.class);
        verify(vacationRepository).save(vacationCaptor.capture());

        assertEquals(expectedEndDate, vacationCaptor.getValue().getEndDate());
    }

    @Test
    void shouldSetVacationAsPendingAndNotApprovedByDefault() {
        // given
        EmployeeEntity validEmployee = buildValidEmployee();

        // when
        vacationService.createVacationWithEmployee(validEmployee, MONDAY);

        // then
        ArgumentCaptor<VacationEntity> vacationCaptor = ArgumentCaptor.forClass(VacationEntity.class);
        verify(vacationRepository).save(vacationCaptor.capture());

        VacationEntity savedVacation = vacationCaptor.getValue();
        assertAll(
                () -> assertEquals(VacationEntity.State.PENDIENTE, savedVacation.getState()),
                () -> assertFalse(savedVacation.isApproved())
        );
    }


    /**
     * tests function findAllPendingVacations
     */
    @Test
    void shouldReturnEmptyListWhenNoPendingVacationsExist() {
        // given
        given(vacationRepository.findAllPendingVacations()).willReturn(Collections.emptyList());

        // when
        List<VacationPendingDto> result = vacationService.findAllPendingVacations();

        // then
        assertTrue(result.isEmpty());
        verify(vacationRepository).findAllPendingVacations();
    }

    @Test
    void shouldReturnAllPendingVacationsWhenTheyExist() {
        // given
        List<VacationPendingDto> expectedVacations = List.of(PENDING_VACATION_1, PENDING_VACATION_2);
        given(vacationRepository.findAllPendingVacations()).willReturn(expectedVacations);

        // when
        List<VacationPendingDto> result = vacationService.findAllPendingVacations();

        // then
        assertEquals(2, result.size());
        assertIterableEquals(expectedVacations, result);
        verify(vacationRepository).findAllPendingVacations();
    }

    @Test
    void shouldReturnOnlyPendingVacations() {
        // given
        VacationPendingDto approvedVacation = PENDING_VACATION_1.toBuilder()
                .approved(true)
                .state(VacationEntity.State.APROVADA)
                .build();

        given(vacationRepository.findAllPendingVacations()).willReturn(List.of(PENDING_VACATION_2));

        // when
        List<VacationPendingDto> result = vacationService.findAllPendingVacations();

        // then
        assertEquals(1, result.size());
        assertEquals(PENDING_VACATION_2, result.get(0));
        verify(vacationRepository).findAllPendingVacations();
    }

    /**
     * tests function updatePendingVacations
     */
    @Test
    void shouldApproveVacationWhenApprovedIsTrue() {
        // given
        VacationEntity pendingVacation = buildPendingVacationEntity();
        List<VacationPendingDto> expectedPendingList = List.of(PENDING_VACATION_2);

        given(vacationRepository.findById(VALID_VACATION_ID)).willReturn(Optional.of(pendingVacation));
        given(vacationRepository.save(any(VacationEntity.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(vacationRepository.findAllPendingVacations()).willReturn(expectedPendingList);

        // when
        List<VacationPendingDto> result = vacationService.updatePendingVacations(VALID_VACATION_ID, APPROVE_REQUEST);

        // then
        ArgumentCaptor<VacationEntity> vacationCaptor = ArgumentCaptor.forClass(VacationEntity.class);
        verify(vacationRepository).save(vacationCaptor.capture());

        VacationEntity savedVacation = vacationCaptor.getValue();
        assertAll(
                () -> assertEquals(true, savedVacation.isApproved()),
                () -> assertEquals(VacationEntity.State.APROVADA, savedVacation.getState())
        );
        assertEquals(expectedPendingList, result);
    }

    @Test
    void shouldRejectVacationWhenApprovedIsFalse() {
        // given
        VacationEntity pendingVacation = buildPendingVacationEntity();
        List<VacationPendingDto> expectedPendingList = List.of(PENDING_VACATION_2);

        given(vacationRepository.findById(VALID_VACATION_ID)).willReturn(Optional.of(pendingVacation));
        given(vacationRepository.save(any(VacationEntity.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(vacationRepository.findAllPendingVacations()).willReturn(expectedPendingList);

        // when
        List<VacationPendingDto> result = vacationService.updatePendingVacations(VALID_VACATION_ID, REJECT_REQUEST);

        // then
        ArgumentCaptor<VacationEntity> vacationCaptor = ArgumentCaptor.forClass(VacationEntity.class);
        verify(vacationRepository).save(vacationCaptor.capture());

        VacationEntity savedVacation = vacationCaptor.getValue();
        assertAll(
                () -> assertEquals(false, savedVacation.isApproved()),
                () -> assertEquals(VacationEntity.State.RECHAZADA, savedVacation.getState())
        );
        assertEquals(expectedPendingList, result);
    }

    @Test
    void shouldThrowExceptionWhenVacationNotFound() {
        // given
        given(vacationRepository.findById(INVALID_VACATION_ID)).willReturn(Optional.empty());

        // when & then
        assertThrows(ValueNotFoundException.class, () ->
                vacationService.updatePendingVacations(INVALID_VACATION_ID, APPROVE_REQUEST));

        verify(vacationRepository, never()).save(any());
        verify(vacationRepository, never()).findAllPendingVacations();
    }

    @Test
    void shouldReturnUpdatedPendingVacationsAfterApproval() {
        // given
        VacationEntity pendingVacation = buildPendingVacationEntity();
        List<VacationPendingDto> expectedPendingList = Collections.emptyList();

        given(vacationRepository.findById(VALID_VACATION_ID)).willReturn(Optional.of(pendingVacation));
        given(vacationRepository.save(any(VacationEntity.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(vacationRepository.findAllPendingVacations()).willReturn(expectedPendingList);

        // when
        List<VacationPendingDto> result = vacationService.updatePendingVacations(VALID_VACATION_ID, APPROVE_REQUEST);

        // then
        assertEquals(expectedPendingList, result);
        verify(vacationRepository).findAllPendingVacations();
    }

    /**
     * tests function findAllApprovedVacations
     */
    @Test
    void shouldReturnEmptyListWhenNoApprovedVacationsExist() {
        // given
        given(vacationRepository.findLastApprovedVacationPerEmployee()).willReturn(Collections.emptyList());

        // when
        List<VacationPendingDto> result = vacationService.findAllApprovedVacations();

        // then
        assertTrue(result.isEmpty());
        verify(vacationRepository).findLastApprovedVacationPerEmployee();
    }

    @Test
    void shouldReturnAllApprovedVacationsWhenTheyExist() {
        // given
        List<VacationPendingDto> expectedVacations = List.of(APPROVED_VACATION_1, APPROVED_VACATION_2);
        given(vacationRepository.findLastApprovedVacationPerEmployee()).willReturn(expectedVacations);

        // when
        List<VacationPendingDto> result = vacationService.findAllApprovedVacations();

        // then
        assertEquals(2, result.size());
        assertIterableEquals(expectedVacations, result);
        verify(vacationRepository).findLastApprovedVacationPerEmployee();
    }

    @Test
    void shouldReturnOnlyApprovedVacations() {
        // given
        given(vacationRepository.findLastApprovedVacationPerEmployee())
                .willReturn(List.of(APPROVED_VACATION_1));

        // when
        List<VacationPendingDto> result = vacationService.findAllApprovedVacations();

        // then
        assertEquals(1, result.size());
        assertEquals(APPROVED_VACATION_1, result.get(0));
        assertTrue(result.get(0).approved());
        assertEquals(VacationEntity.State.APROVADA, result.get(0).state());
        verify(vacationRepository).findLastApprovedVacationPerEmployee();
    }

    @Test
    void shouldReturnMostRecentApprovedVacationPerEmployee() {
        // given
        VacationPendingDto olderApprovedVacation = APPROVED_VACATION_1.toBuilder()
                .id(3L)
                .requestedDate(MONDAY.minusDays(60))
                .startDate(NEXT_YEAR_MONDAY.minusDays(60))
                .endDate(EXPECTED_END_DATE.minusDays(60))
                .build();

        given(vacationRepository.findLastApprovedVacationPerEmployee())
                .willReturn(List.of(APPROVED_VACATION_1, APPROVED_VACATION_2));

        // when
        List<VacationPendingDto> result = vacationService.findAllApprovedVacations();

        // then
        assertEquals(2, result.size());
        // Verify that the more recent vacation is returned (implied by repository mock)
        verify(vacationRepository, never()).findAll(); // Ensure we're using the correct repository method
        verify(vacationRepository).findLastApprovedVacationPerEmployee();
    }

    private VacationEntity buildPendingVacationEntity() {
        return VacationEntity.builder()
                .id(VALID_VACATION_ID)
                .requestedDate(MONDAY)
                .startDate(NEXT_YEAR_MONDAY)
                .endDate(EXPECTED_END_DATE)
                .approved(false)
                .state(VacationEntity.State.PENDIENTE)
                .employee(buildValidEmployee())
                .build();
    }

    private EmployeeEntity buildValidEmployee() {
        return EmployeeEntity.builder()
                .id(1L)
                .fullName("John Doe")
                .cui("1234567890123")
                .email("john.doe@company.com")
                .isSpecialist(false)
                .area(AreaEntity.builder().id(1L).name("IT").build())
                .build();
    }

    private static VacationPendingDto builderVaction(){
        return VacationPendingDto.builder()
                .employeeId(1L)
                .fullName("John Doe")
                .cui("1234567890123")
                .name("IT Department")
                .id(1L)
                .requestedDate(MONDAY)
                .startDate(NEXT_YEAR_MONDAY)
                .endDate(EXPECTED_END_DATE)
                .approved(false)
                .state(VacationEntity.State.PENDIENTE)
                .build();
    }



}