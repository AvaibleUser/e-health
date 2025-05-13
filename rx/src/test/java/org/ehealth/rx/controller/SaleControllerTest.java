package org.ehealth.rx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.rx.domain.dto.report.*;
import org.ehealth.rx.service.SaleService;
import org.ehealth.rx.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.ehealth.rx.util.JwtBuilder.jwt;
import static org.ehealth.rx.util.ThenMockAlias.thenMock;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(classes = SaleController.class, controllers = SaleController.class)
class SaleControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected SaleService saleService;

    // URL Constants
    private static final String BASE_URL = "/api/rx/v1/sales";
    private static final String API_CONTEXT_PATH = "/api/rx";
    private static final String REPORT_MEDICINE_ENDPOINT = "/report/medicine";
    private static final String REPORT_EMPLOYEES_ENDPOINT = "/report/employees";
    private static final String REPORT_INCOME_ENDPOINT = "/report/income";

    // Date Constants
    private static final LocalDate START_DATE_2024 = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE_2024 = LocalDate.of(2024, 12, 31);

    // Medicine Constants
    private static final Long MEDICINE_ID_1 = 1L;
    private static final String MEDICINE_NAME_PARACETAMOL = "Paracetamol";
    private static final int MEDICINE_TOTAL_SOLD_10 = 10;

    // Employee Constants
    private static final Long EMPLOYEE_ID_1 = 1L;
    private static final String EMPLOYEE_NAME_JUAN = "Juan Perez";
    private static final String EMPLOYEE_CUI_1 = "1";
    private static final int EMPLOYEE_TOTAL_SOLD_5 = 5;

    // Financial Constants
    private static final BigDecimal INCOME_10 = BigDecimal.TEN;
    private static final BigDecimal INCOME_100 = BigDecimal.valueOf(100);
    private static final BigDecimal INCOME_1000 = BigDecimal.valueOf(1000);
    private static final BigDecimal PROFIT_1 = BigDecimal.ONE;
    private static final BigDecimal PROFIT_10 = BigDecimal.TEN;

    // Role Constants
    private static final String ROLE_EMPLOYEE = "EMPLOYEE";

    // Empty Lists
    private static final List<ItemsSaleMedicineDto> EMPTY_MEDICINE_ITEMS = List.of();
    private static final List<ItemsSalePerEmployeeDto> EMPTY_EMPLOYEE_ITEMS = List.of();
    private static final List<SaleMedicineDto> EMPTY_TOTAL_ITEMS = List.of();

    @Test
    void getReportSalesMedicinePerMedicineInRange_shouldReturnList() throws Exception {
        // given
        ReportSaleMedicineDto medicineReport = ReportSaleMedicineDto.builder()
                .medicineId(MEDICINE_ID_1)
                .name(MEDICINE_NAME_PARACETAMOL)
                .totalSold(MEDICINE_TOTAL_SOLD_10)
                .totalIncome(INCOME_10)
                .totalProfit(PROFIT_1)
                .items(EMPTY_MEDICINE_ITEMS)
                .build();

        List<ReportSaleMedicineDto> expectedResponse = List.of(medicineReport);

        given(saleService.getReportSalesMedicinePerMedicineInRange(START_DATE_2024, END_DATE_2024))
                .willReturn(expectedResponse);

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL + REPORT_MEDICINE_ENDPOINT)
                .contextPath(API_CONTEXT_PATH)
                .with(jwt(EMPLOYEE_ID_1, ROLE_EMPLOYEE))
                .param("startDate", START_DATE_2024.toString())
                .param("endDate", END_DATE_2024.toString()));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse)));

        thenMock(saleService).should().getReportSalesMedicinePerMedicineInRange(START_DATE_2024, END_DATE_2024);
    }

    @Test
    void getReportSalesMedicineEmployeeInRange_shouldReturnList() throws Exception {
        // given
        ReportSalesPerEmployeeDto employeeReport = ReportSalesPerEmployeeDto.builder()
                .employeeId(EMPLOYEE_ID_1)
                .employeeName(EMPLOYEE_NAME_JUAN)
                .cui(EMPLOYEE_CUI_1)
                .totalSold(EMPLOYEE_TOTAL_SOLD_5)
                .totalIncome(INCOME_100)
                .totalProfit(PROFIT_10)
                .items(EMPTY_EMPLOYEE_ITEMS)
                .build();

        List<ReportSalesPerEmployeeDto> expectedResponse = List.of(employeeReport);

        given(saleService.getReportSalesMedicineEmployeeInRange(START_DATE_2024, END_DATE_2024))
                .willReturn(expectedResponse);

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL + REPORT_EMPLOYEES_ENDPOINT)
                .contextPath(API_CONTEXT_PATH)
                .with(jwt(EMPLOYEE_ID_1, ROLE_EMPLOYEE))
                .param("startDate", START_DATE_2024.toString())
                .param("endDate", END_DATE_2024.toString()));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse)));

        thenMock(saleService).should().getReportSalesMedicineEmployeeInRange(START_DATE_2024, END_DATE_2024);
    }

    @Test
    void getReportSalesTotalInRange_shouldReturnSummary() throws Exception {
        // given
        ReportSalesTotal totalReport = ReportSalesTotal.builder()
                .totalIncome(INCOME_1000)
                .items(EMPTY_TOTAL_ITEMS)
                .build();

        given(saleService.getReportSalesTotalInRange(START_DATE_2024, END_DATE_2024))
                .willReturn(totalReport);

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL + REPORT_INCOME_ENDPOINT)
                .contextPath(API_CONTEXT_PATH)
                .with(jwt(EMPLOYEE_ID_1, ROLE_EMPLOYEE))
                .param("startDate", START_DATE_2024.toString())
                .param("endDate", END_DATE_2024.toString()));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(totalReport)));

        thenMock(saleService).should().getReportSalesTotalInRange(START_DATE_2024, END_DATE_2024);
    }
}