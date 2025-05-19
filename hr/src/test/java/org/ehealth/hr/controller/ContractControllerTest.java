package org.ehealth.hr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.hr.domain.dto.ContractDto;
import org.ehealth.hr.domain.dto.FinishContractDto;
import org.ehealth.hr.domain.dto.NewContractDto;
import org.ehealth.hr.domain.dto.UpdateSalaryDto;
import org.ehealth.hr.domain.dto.reports.ReportEmployeeContracts;
import org.ehealth.hr.service.ContractService;
import org.ehealth.hr.util.ControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


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

@ControllerTest(classes = ContractController.class, controllers = ContractController.class)
class ContractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected ContractService contractService;

    private static final Long CONTRACT_ID = 1L;
    private static final Long EMPLOYEE_ID = 2L;
    private static final Long AREA_ID = 3L;
    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");
    private static final LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2024, 12, 31);

    private static final String DESCRIPTION_FINISH = "termination";
    private static final String DESCRIPTION = "despido";
    private static final String CUI = "123456789";



    @Test
    void getContractByEmployeeId_shouldReturnContract() throws Exception {
        // given
        ContractDto dto = ContractDto.builder().id(CONTRACT_ID).employeeId(EMPLOYEE_ID).salary(BigDecimal.TEN).build();
        given(contractService.getContractByEmployeeId(EMPLOYEE_ID)).willReturn(dto);

        // when
        mockMvc.perform(get("/api/hr/v1/contracts/latest/employee/{employeeId}", EMPLOYEE_ID)
                        .contextPath("/api/hr")
                        .with(jwt(1L, "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dto)));
        // then
        thenMock(contractService).should().getContractByEmployeeId(EMPLOYEE_ID);
    }

    @Test
    void createNewContract_shouldReturnCreated() throws Exception {
        // given
        NewContractDto dto = NewContractDto.builder()
                .idContract(CONTRACT_ID).idEmployee(EMPLOYEE_ID).salary(BigDecimal.TEN)
                .igssDiscount(BigDecimal.ONE).irtraDiscount(BigDecimal.ONE).build();

        // when
        mockMvc.perform(post("/api/hr/v1/contracts")
                        .contextPath("/api/hr")
                        .with(jwt(1L, "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // then
        thenMock(contractService).should().createNewContract(dto);
    }

    @Test
    void finishContract_shouldReturnOk() throws Exception {
        // given
        FinishContractDto dto = new FinishContractDto(DESCRIPTION_FINISH, CUI);

        // when
        mockMvc.perform(patch("/api/hr/v1/contracts/finish/{contractId}", CONTRACT_ID)
                        .contextPath("/api/hr")
                        .with(jwt(1L, "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // then
        thenMock(contractService).should().finishContract(CONTRACT_ID, dto);
    }

    @Test
    void updateSalary_shouldReturnOk() throws Exception {
        // given
        UpdateSalaryDto dto = new UpdateSalaryDto(BigDecimal.valueOf(2000), true);

        // when
        mockMvc.perform(put("/api/hr/v1/contracts/update-salary/{contractId}", CONTRACT_ID)
                        .contextPath("/api/hr")
                        .with(jwt(1L, "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // then
        thenMock(contractService).should().updateSalary(CONTRACT_ID, dto);
    }

    @Test
    void dismissalWork_shouldReturnOk() throws Exception {
        //given
        FinishContractDto dto = new FinishContractDto(DESCRIPTION,CUI);

        //when
        mockMvc.perform(patch("/api/hr/v1/contracts/dismissal-work/{contractId}", CONTRACT_ID)
                        .contextPath("/api/hr")
                        .with(jwt(1L, "ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        //then
        thenMock(contractService).should().dismissalWork(CONTRACT_ID, dto);
    }

    @Test
    void getAllContracts_shouldReturnList() throws Exception {
        //given
        List<ContractDto> contracts = List.of(
                ContractDto.builder().id(1L).employeeId(EMPLOYEE_ID).salary(BigDecimal.TEN).build(),
                ContractDto.builder().id(2L).employeeId(EMPLOYEE_ID).salary(BigDecimal.valueOf(20)).build()
        );

        given(contractService.findAllContractsOrderedByCreationDate(EMPLOYEE_ID)).willReturn(contracts);

        //when
        mockMvc.perform(get("/api/hr/v1/contracts/history/employee/{employeeId}", EMPLOYEE_ID)
                        .contextPath("/api/hr")
                        .with(jwt(1L, "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(contracts)));

        //then
        thenMock(contractService).should().findAllContractsOrderedByCreationDate(EMPLOYEE_ID);
    }

    @Test
    void getEmployeeContractReport_shouldReturnReport() throws Exception {
        //given
        ReportEmployeeContracts report = ReportEmployeeContracts.builder().report(List.of()).build();

        given(contractService.reportEmployeeContracts(AREA_ID, START_DATE, END_DATE)).willReturn(report);

        //when
        mockMvc.perform(get("/api/hr/v1/contracts/reports/employees/history/{areaId}", AREA_ID)
                        .contextPath("/api/hr")
                        .param("startDate", START_DATE.toString())
                        .param("endDate", END_DATE.toString())
                        .with(jwt(1L, "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(report)));

        //then
        thenMock(contractService).should().reportEmployeeContracts(AREA_ID, START_DATE, END_DATE);
    }

    @Test
    void getReportTerminatedContracts_shouldReturnReport() throws Exception {
        //given
        ReportEmployeeContracts report = ReportEmployeeContracts.builder().report(List.of()).build();

        given(contractService.reportTerminatedContracts(AREA_ID, START_DATE, END_DATE)).willReturn(report);

        //when
        mockMvc.perform(get("/api/hr/v1/contracts/reports/employees/history/terminated/{areaId}", AREA_ID)
                        .contextPath("/api/hr")
                        .param("startDate", START_DATE.toString())
                        .param("endDate", END_DATE.toString())
                        .with(jwt(1L, "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(report)));

        //then
        thenMock(contractService).should().reportTerminatedContracts(AREA_ID, START_DATE, END_DATE);
    }

}