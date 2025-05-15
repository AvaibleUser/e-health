package org.ehealth.ward.controller.or;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.ward.domain.dto.or.surgery.SurgeryPaymentDto;
import org.ehealth.ward.service.or.SurgeryService;
import org.ehealth.ward.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.ehealth.ward.util.JwtBuilder.jwt;
import static org.ehealth.ward.util.ThenMockAlias.thenMock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(classes = SurgeryController.class, controllers = SurgeryController.class)
class SurgeryControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected SurgeryService surgeryService;

    // URL Constants
    private static final String BASE_URL = "/api/ward/v1/surgeries";
    private static final String PAYMENTS_ENDPOINT = "/payments";
    private static final String EXIST_ENDPOINT = "/exist";
    private static final String API_CONTEXT_PATH = "/api/ward";

    // Surgery Constants
    private static final Long SURGERY_ID_1 = 1L;
    private static final Long SURGERY_ID_2 = 2L;
    private static final String SURGERY_APPENDECTOMY = "Appendectomy";
    private static final String SURGERY_CHOLECYSTECTOMY = "Cholecystectomy";
    private static final BigDecimal APPENDECTOMY_FEE = new BigDecimal("120.00");
    private static final BigDecimal CHOLECYSTECTOMY_FEE = new BigDecimal("150.50");

    // Specialist Constants
    private static final Long SPECIALIST_ID_10 = 10L;
    private static final Long SPECIALIST_ID_100 = 100L;
    private static final Long SPECIALIST_ID_101 = 101L;

    // Date Constants
    private static final LocalDate DATE_2024_05_01 = LocalDate.of(2024, 5, 1);
    private static final LocalDate DATE_2024_05_03 = LocalDate.of(2024, 5, 3);

    // User Constants
    private static final Long ADMIN_USER_ID = 1L;
    private static final String ROLE_ADMIN = "ADMIN";

    @Test
    void getSurgeryPaymentDto_shouldReturnList() throws Exception {
        // given
        SurgeryPaymentDto appendectomyPayment = SurgeryPaymentDto.builder()
                .specialistFee(APPENDECTOMY_FEE)
                .id(SURGERY_ID_1)
                .description(SURGERY_APPENDECTOMY)
                .performedDate(DATE_2024_05_01)
                .employeeId(SPECIALIST_ID_100)
                .build();

        SurgeryPaymentDto cholecystectomyPayment = SurgeryPaymentDto.builder()
                .specialistFee(CHOLECYSTECTOMY_FEE)
                .id(SURGERY_ID_2)
                .description(SURGERY_CHOLECYSTECTOMY)
                .performedDate(DATE_2024_05_03)
                .employeeId(SPECIALIST_ID_101)
                .build();

        List<SurgeryPaymentDto> expectedPayments = List.of(appendectomyPayment, cholecystectomyPayment);

        given(surgeryService.getSurgeryPaymentDto()).willReturn(expectedPayments);

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL + PAYMENTS_ENDPOINT)
                .contextPath(API_CONTEXT_PATH)
                .with(jwt(ADMIN_USER_ID, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedPayments)));

        thenMock(surgeryService).should().getSurgeryPaymentDto();
    }

    @Test
    void existSurgeryPayment_shouldReturnTrue() throws Exception {
        // given
        given(surgeryService.existSurgeryPayment(SPECIALIST_ID_10)).willReturn(true);

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL + EXIST_ENDPOINT + "/{specialistId}", SPECIALIST_ID_10)
                .contextPath(API_CONTEXT_PATH)
                .with(jwt(ADMIN_USER_ID, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("true"));

        thenMock(surgeryService).should().existSurgeryPayment(SPECIALIST_ID_10);
    }
}
