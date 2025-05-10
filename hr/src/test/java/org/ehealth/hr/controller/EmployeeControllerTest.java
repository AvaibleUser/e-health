package org.ehealth.hr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.hr.domain.dto.CreateEmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeResponseDto;
import org.ehealth.hr.domain.dto.reports.ReportAssignedEmployeeDto;
import org.ehealth.hr.service.EmployeeService;
import org.ehealth.hr.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
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


@ControllerTest(classes = EmployeeController.class, controllers = EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected EmployeeService employeeService;

    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-01T00:00:00Z");

    // Employee constants
    private static final Long EMPLOYEE_ID_1 = 1L;
    private static final Long EMPLOYEE_ID_2 = 2L;
    private static final Long EMPLOYEE_ID_3 = 3L;
    private static final String EMPLOYEE_FULL_NAME_JOHN = "John Doe";
    private static final String EMPLOYEE_FULL_NAME_JANE = "Jane Smith";
    private static final String EMPLOYEE_FULL_NAME_CARLOS = "Carlos";
    private static final String EMPLOYEE_FULL_NAME_A = "A";
    private static final String EMPLOYEE_FULL_NAME_B = "B";
    private static final String CUI_JOHN = "1234567890101";
    private static final String CUI_JANE = "9876543210001";
    private static final String CUI_CARLOS = "999";
    private static final String CUI_A = "1";
    private static final String CUI_B = "2";
    private static final String PHONE_JOHN = "555-1234";
    private static final String PHONE_JANE = "555-4321";
    private static final String PHONE_CARLOS = "321";
    private static final String PHONE_A = "p";
    private static final String PHONE_B = "q";
    private static final String EMAIL_JOHN = "john@example.com";
    private static final String EMAIL_JANE = "jane@example.com";
    private static final String EMAIL_CARLOS = "c@c.com";
    private static final String EMAIL_A = "a@a.com";
    private static final String EMAIL_B = "b@b.com";
    private static final String AREA_CARDIOLOGY = "Cardiology";
    private static final String AREA_ONCOLOGY = "Oncology";
    private static final String AREA_X = "X";
    private static final String AREA_Y = "Y";

    // Test constants
    private static final Long AREA_ID_1 = 1L;
    private static final Long AREA_ID_5 = 5L;
    private static final Long USER_ID_1 = 1L;
    private static final String ROLE_ADMIN = "ADMIN";
    private static final LocalDate START_DATE_2024_05_01 = LocalDate.parse("2024-05-01");
    private static final BigDecimal SALARY_3000 = BigDecimal.valueOf(3000);
    private static final BigDecimal IGSS_DISCOUNT_100 = BigDecimal.valueOf(100);
    private static final BigDecimal IRTRA_DISCOUNT_50 = BigDecimal.valueOf(50);
    private static final String START_DATE_2024_01_01 = "2024-01-01";
    private static final String END_DATE_2024_12_31 = "2024-12-31";
    private static final int FILTER_1 = 1;

    @Test
    void findEmployeeByCui_shouldReturnEmployee() throws Exception {
        // given
        EmployeeDto employeeDto = EmployeeDto.builder()
                .id(EMPLOYEE_ID_1)
                .fullName(EMPLOYEE_FULL_NAME_JOHN)
                .cui(CUI_JOHN)
                .phone(PHONE_JOHN)
                .email(EMAIL_JOHN)
                .isSpecialist(true)
                .areaName(AREA_CARDIOLOGY)
                .createdAt(FIXED_INSTANT)
                .updatedAt(FIXED_INSTANT)
                .build();

        given(employeeService.findEmployeeByCui(CUI_JOHN)).willReturn(employeeDto);

        // when
        ResultActions result = mockMvc.perform(get("/api/hr/v1/employees/cui/{cui}", CUI_JOHN)
                .contextPath("/api/hr")
                .with(jwt(USER_ID_1, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(employeeDto)));

        thenMock(employeeService).should().findEmployeeByCui(CUI_JOHN);
    }

    @Test
    void createEmployee_shouldReturnCreated() throws Exception {
        // given
        CreateEmployeeDto createEmployeeDto = CreateEmployeeDto.builder()
                .fullName(EMPLOYEE_FULL_NAME_JANE)
                .cui(CUI_JANE)
                .phone(PHONE_JANE)
                .email(EMAIL_JANE)
                .area(AREA_ID_1)
                .isSpecialist(false)
                .startDate(START_DATE_2024_05_01)
                .salary(SALARY_3000)
                .igssDiscount(IGSS_DISCOUNT_100)
                .irtraDiscount(IRTRA_DISCOUNT_50)
                .build();

        EmployeeResponseDto responseDto = EmployeeResponseDto.builder()
                .id(EMPLOYEE_ID_2)
                .fullName(EMPLOYEE_FULL_NAME_JANE)
                .cui(CUI_JANE)
                .phone(PHONE_JANE)
                .email(EMAIL_JANE)
                .isSpecialist(false)
                .createdAt(FIXED_INSTANT)
                .build();

        given(employeeService.createEmployee(createEmployeeDto)).willReturn(responseDto);

        // when
        ResultActions result = mockMvc.perform(post("/api/hr/v1/employees")
                .contextPath("/api/hr")
                .with(csrf())
                .with(jwt(USER_ID_1, ROLE_ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createEmployeeDto)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(responseDto)));

        thenMock(employeeService).should().createEmployee(createEmployeeDto);
    }

    @Test
    void findAllEmployees_shouldReturnList() throws Exception {
        // given
        List<EmployeeDto> employees = List.of(
                EmployeeDto.builder()
                        .id(EMPLOYEE_ID_1)
                        .fullName(EMPLOYEE_FULL_NAME_A)
                        .cui(CUI_A)
                        .phone(PHONE_A)
                        .email(EMAIL_A)
                        .isSpecialist(false)
                        .areaName(AREA_X)
                        .createdAt(FIXED_INSTANT)
                        .updatedAt(FIXED_INSTANT)
                        .build(),
                EmployeeDto.builder()
                        .id(EMPLOYEE_ID_2)
                        .fullName(EMPLOYEE_FULL_NAME_B)
                        .cui(CUI_B)
                        .phone(PHONE_B)
                        .email(EMAIL_B)
                        .isSpecialist(true)
                        .areaName(AREA_Y)
                        .createdAt(FIXED_INSTANT)
                        .updatedAt(FIXED_INSTANT)
                        .build()
        );

        given(employeeService.findAllEmployeesOrdered()).willReturn(employees);

        // when
        ResultActions result = mockMvc.perform(get("/api/hr/v1/employees")
                .contextPath("/api/hr")
                .with(jwt(USER_ID_1, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(employees)));

        thenMock(employeeService).should().findAllEmployeesOrdered();
    }

    @Test
    void findEmployeesByArea_shouldReturnList() throws Exception {
        // given
        List<EmployeeDto> employees = List.of(
                EmployeeDto.builder()
                        .id(EMPLOYEE_ID_3)
                        .fullName(EMPLOYEE_FULL_NAME_CARLOS)
                        .cui(CUI_CARLOS)
                        .phone(PHONE_CARLOS)
                        .email(EMAIL_CARLOS)
                        .isSpecialist(true)
                        .areaName(AREA_ONCOLOGY)
                        .createdAt(FIXED_INSTANT)
                        .updatedAt(FIXED_INSTANT)
                        .build()
        );

        given(employeeService.findEmployeesByArea(AREA_ID_5)).willReturn(employees);

        // when
        ResultActions result = mockMvc.perform(get("/api/hr/v1/employees/area/{areaId}", AREA_ID_5)
                .contextPath("/api/hr")
                .with(jwt(USER_ID_1, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(employees)));

        thenMock(employeeService).should().findEmployeesByArea(AREA_ID_5);
    }

    @Test
    void getAssignedReport_shouldReturnReport() throws Exception {
        // given
        ReportAssignedEmployeeDto reportDto = ReportAssignedEmployeeDto.builder()
                .report(List.of())
                .build();

        given(employeeService.getReportAssignedEmployeeInRange(FILTER_1, START_DATE_2024_01_01, END_DATE_2024_12_31))
                .willReturn(reportDto);

        // when
        ResultActions result = mockMvc.perform(get("/api/hr/v1/employees/assigned/report/doctors/{filter}", FILTER_1)
                .param("startDate", START_DATE_2024_01_01)
                .param("endDate", END_DATE_2024_12_31)
                .contextPath("/api/hr")
                .with(jwt(USER_ID_1, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(reportDto)));

        thenMock(employeeService).should().getReportAssignedEmployeeInRange(FILTER_1, START_DATE_2024_01_01, END_DATE_2024_12_31);
    }
}