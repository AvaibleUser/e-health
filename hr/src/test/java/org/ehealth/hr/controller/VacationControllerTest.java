package org.ehealth.hr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.hr.domain.dto.vacation.CreateRequestVacationDto;
import org.ehealth.hr.domain.dto.vacation.UpdateRequestVacationDto;
import org.ehealth.hr.domain.dto.vacation.VacationPendingDto;
import org.ehealth.hr.domain.entity.VacationEntity;
import org.ehealth.hr.service.VacationService;
import org.ehealth.hr.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.ehealth.hr.util.JwtBuilder.jwt;
import static org.ehealth.hr.util.ThenMockAlias.thenMock;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@ControllerTest(classes = VacationController.class, controllers = VacationController.class)
class VacationControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected VacationService vacationService;

    // Employee constants
    private static final Long EMPLOYEE_ID_1 = 1L;
    private static final Long EMPLOYEE_ID_2 = 2L;
    private static final String EMPLOYEE_FULL_NAME_JUAN = "Juan Perez";
    private static final String EMPLOYEE_FULL_NAME_MARIA = "Maria Lopez";
    private static final String EMPLOYEE_CUI_JUAN = "123456789";
    private static final String EMPLOYEE_CUI_MARIA = "987654321";

    // Department constants
    private static final String DEPARTMENT_IT = "IT";
    private static final String DEPARTMENT_HR = "HR";

    // Vacation constants
    private static final Long VACATION_ID_100 = 100L;
    private static final Long VACATION_ID_101 = 101L;
    private static final Long VACATION_ID_102 = 102L;
    private static final Long VACATION_ID_1 = 1L;
    private static final int VACATION_DAYS_5 = 5;

    // Date constants
    private static final LocalDate DATE_2024_01_01 = LocalDate.of(2024, 1, 1);
    private static final LocalDate DATE_2024_01_10 = LocalDate.of(2024, 1, 10);
    private static final LocalDate DATE_2024_01_15 = LocalDate.of(2024, 1, 15);
    private static final LocalDate DATE_2024_01_20 = LocalDate.of(2024, 1, 20);
    private static final LocalDate DATE_2024_02_01 = LocalDate.of(2024, 2, 1);
    private static final LocalDate DATE_2024_02_06 = LocalDate.of(2024, 2, 6);
    private static final LocalDate DATE_2024_03_01 = LocalDate.of(2024, 3, 1);
    private static final LocalDate DATE_2024_03_10 = LocalDate.of(2024, 3, 10);
    private static final LocalDate DATE_2024_03_20 = LocalDate.of(2024, 3, 20);

    // Role constants
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_EMPLOYEE = "EMPLOYEE";

    // State constants
    private static final VacationEntity.State STATE_PENDING = VacationEntity.State.PENDIENTE;
    private static final VacationEntity.State STATE_APPROVED = VacationEntity.State.APROVADA;

    @Test
    void getAllPendingVacations_shouldReturnList() throws Exception {
        // given
        VacationPendingDto pendingVacation = VacationPendingDto.builder()
                .employeeId(EMPLOYEE_ID_1)
                .fullName(EMPLOYEE_FULL_NAME_JUAN)
                .cui(EMPLOYEE_CUI_JUAN)
                .name(DEPARTMENT_IT)
                .id(VACATION_ID_100)
                .requestedDate(DATE_2024_01_01)
                .startDate(DATE_2024_01_10)
                .endDate(DATE_2024_01_20)
                .approved(false)
                .state(STATE_PENDING)
                .build();

        List<VacationPendingDto> expectedPendingVacations = List.of(pendingVacation);

        given(vacationService.findAllPendingVacations()).willReturn(expectedPendingVacations);

        // when
        ResultActions result = mockMvc.perform(get("/api/hr/v1/vacations/pending")
                .contextPath("/api/hr")
                .with(jwt(EMPLOYEE_ID_1, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedPendingVacations)));

        thenMock(vacationService).should().findAllPendingVacations();
    }

    @Test
    void updatePendingVacation_shouldReturnUpdatedList() throws Exception {
        // given
        UpdateRequestVacationDto updateDto = new UpdateRequestVacationDto(true);

        VacationPendingDto approvedVacation = VacationPendingDto.builder()
                .employeeId(EMPLOYEE_ID_1)
                .fullName(EMPLOYEE_FULL_NAME_JUAN)
                .cui(EMPLOYEE_CUI_JUAN)
                .name(DEPARTMENT_IT)
                .id(VACATION_ID_100)
                .requestedDate(DATE_2024_01_01)
                .startDate(DATE_2024_01_10)
                .endDate(DATE_2024_01_20)
                .approved(true)
                .state(STATE_APPROVED)
                .build();

        List<VacationPendingDto> expectedUpdatedVacations = List.of(approvedVacation);

        given(vacationService.updatePendingVacations(VACATION_ID_1, updateDto)).willReturn(expectedUpdatedVacations);

        // when
        ResultActions result = mockMvc.perform(patch("/api/hr/v1/vacations/update-state/{vacationId}", VACATION_ID_1)
                .contextPath("/api/hr")
                .with(jwt(EMPLOYEE_ID_1, ROLE_ADMIN))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateDto)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedUpdatedVacations)));

        thenMock(vacationService).should().updatePendingVacations(VACATION_ID_1, updateDto);
    }

    @Test
    void createVacationRequest_shouldReturnCreated() throws Exception {
        // given
        CreateRequestVacationDto createRequest = CreateRequestVacationDto.builder()
                .employeeId(EMPLOYEE_ID_1)
                .startDate(DATE_2024_02_01)
                .days(VACATION_DAYS_5)
                .build();

        VacationPendingDto newVacationRequest = VacationPendingDto.builder()
                .employeeId(EMPLOYEE_ID_1)
                .fullName(EMPLOYEE_FULL_NAME_JUAN)
                .cui(EMPLOYEE_CUI_JUAN)
                .name(DEPARTMENT_IT)
                .id(VACATION_ID_101)
                .requestedDate(DATE_2024_01_15)
                .startDate(DATE_2024_02_01)
                .endDate(DATE_2024_02_06)
                .approved(false)
                .state(STATE_PENDING)
                .build();

        given(vacationService.createRequestVacation(createRequest)).willReturn(newVacationRequest);

        // when
        ResultActions result = mockMvc.perform(post("/api/hr/v1/vacations/request")
                .contextPath("/api/hr")
                .with(jwt(EMPLOYEE_ID_1, ROLE_EMPLOYEE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createRequest)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(newVacationRequest)));

        thenMock(vacationService).should().createRequestVacation(createRequest);
    }

    @Test
    void getAllApprovedVacations_shouldReturnList() throws Exception {
        // given
        VacationPendingDto approvedVacation = VacationPendingDto.builder()
                .employeeId(EMPLOYEE_ID_2)
                .fullName(EMPLOYEE_FULL_NAME_MARIA)
                .cui(EMPLOYEE_CUI_MARIA)
                .name(DEPARTMENT_HR)
                .id(VACATION_ID_102)
                .requestedDate(DATE_2024_03_01)
                .startDate(DATE_2024_03_10)
                .endDate(DATE_2024_03_20)
                .approved(true)
                .state(STATE_APPROVED)
                .build();

        List<VacationPendingDto> approvedVacations = List.of(approvedVacation);

        given(vacationService.findAllApprovedVacations()).willReturn(approvedVacations);

        // when
        ResultActions result = mockMvc.perform(get("/api/hr/v1/vacations/approved")
                .contextPath("/api/hr")
                .with(jwt(EMPLOYEE_ID_1, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(approvedVacations)));

        thenMock(vacationService).should().findAllApprovedVacations();
    }
}