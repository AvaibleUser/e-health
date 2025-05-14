package org.ehealth.ward.controller.ward;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.ward.domain.dto.client.employee.EmployeeDto;
import org.ehealth.ward.domain.dto.ward.employee.AssignedEmployeeDto;
import org.ehealth.ward.domain.dto.ward.employee.AssignedEmployeeReportDto;
import org.ehealth.ward.domain.dto.ward.employee.CompleteEmployeeDto;
import org.ehealth.ward.service.ward.AssignedEmployeeService;
import org.ehealth.ward.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.ehealth.ward.util.JwtBuilder.jwt;
import static org.ehealth.ward.util.ThenMockAlias.thenMock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(classes = AssignedEmployeeController.class, controllers = AssignedEmployeeController.class)
class AssignedEmployeeControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected AssignedEmployeeService service;

    private static final Long EMPLOYEE_ID = 10L;
    private static final String CUI = "1234567890101";
    private static final String FULL_NAME = "Dr. Alice Doe";
    private static final LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2024, 12, 31);
    private static final LocalDate ADMISSION_DATE = LocalDate.of(2024, 2, 1);
    private static final LocalDate DISCHARGE_DATE = LocalDate.of(2024, 3, 1);

    @Test
    void getDoctorsAssignedReport_shouldReturnList() throws Exception {
        // given
        List<AssignedEmployeeReportDto> reportList = List.of(
                AssignedEmployeeReportDto.builder()
                        .employeeId(EMPLOYEE_ID)
                        .admissionDate(ADMISSION_DATE)
                        .dischargeDate(DISCHARGE_DATE)
                        .fullName(FULL_NAME)
                        .cui(CUI)
                        .build()
        );

        given(service.getAssignedDoctorsReport(START_DATE, END_DATE)).willReturn(reportList);

        // when
        ResultActions result = mockMvc.perform(get("/api/ward/v1/assigned-employees/report/doctors")
                .contextPath("/api/ward")
                .param("startDate", START_DATE.toString())
                .param("endDate", END_DATE.toString())
                .with(jwt(1L, "ADMIN")));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(reportList)));

        thenMock(service).should().getAssignedDoctorsReport(START_DATE, END_DATE);
    }

}
