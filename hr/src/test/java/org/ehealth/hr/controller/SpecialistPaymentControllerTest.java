package org.ehealth.hr.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.hr.domain.dto.or.PaymentPerSurgeryDto;
import org.ehealth.hr.service.SpecialistPaymentService;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(classes = SpecialistPaymentController.class, controllers = SpecialistPaymentController.class)
class SpecialistPaymentControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected SpecialistPaymentService specialistPaymentService;

    // Path constants
    private static final String BASE_PATH = "/api/hr/v1/payments";
    private static final String API_CONTEXT_PATH = "/api/hr";

    // Specialist constants
    private static final Long SPECIALIST_ID_1 = 1L;
    private static final Long SPECIALIST_ID_10 = 10L;
    private static final Long SPECIALIST_ID_20 = 20L;
    private static final String SPECIALIST_NAME_HOUSE = "Dr. House";
    private static final String SPECIALIST_NAME_STRANGE = "Dr. Strange";
    private static final String SPECIALIST_CUI_HOUSE = "123456789";
    private static final String SPECIALIST_CUI_STRANGE = "987654321";

    // Payment constants
    private static final Long PAYMENT_ID_1 = 1L;
    private static final Long PAYMENT_ID_2 = 2L;
    private static final BigDecimal FEE_100 = new BigDecimal("100.00");
    private static final BigDecimal FEE_200 = new BigDecimal("200.00");

    // Surgery constants
    private static final String SURGERY_APPENDECTOMY = "Appendectomy";
    private static final String SURGERY_CHOLECYSTECTOMY = "Cholecystectomy";

    // Date constants
    private static final LocalDate DATE_2024_05_01 = LocalDate.of(2024, 5, 1);
    private static final LocalDate DATE_2024_04_10 = LocalDate.of(2024, 4, 10);

    // Role constants
    private static final String ROLE_ADMIN = "ADMIN";

    @Test
    void getPaymentPerSurgery_shouldReturnList() throws Exception {
        // given
        PaymentPerSurgeryDto payment = PaymentPerSurgeryDto.builder()
                .specialistFee(FEE_100)
                .id(PAYMENT_ID_1)
                .description(SURGERY_APPENDECTOMY)
                .performedDate(DATE_2024_05_01)
                .employeeId(SPECIALIST_ID_10)
                .fullName(SPECIALIST_NAME_HOUSE)
                .Cui(SPECIALIST_CUI_HOUSE)
                .build();

        List<PaymentPerSurgeryDto> expectedPayments = List.of(payment);

        given(specialistPaymentService.getPaymentPerSurgery()).willReturn(expectedPayments);

        // when
        ResultActions result = mockMvc.perform(get(BASE_PATH)
                .contextPath(API_CONTEXT_PATH)
                .with(jwt(SPECIALIST_ID_1, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedPayments)));

        thenMock(specialistPaymentService).should().getPaymentPerSurgery();
    }

    @Test
    void createPaymentPerSurgery_shouldReturnCreated() throws Exception {
        // given
        PaymentPerSurgeryDto paymentRequest = PaymentPerSurgeryDto.builder()
                .specialistFee(FEE_200)
                .id(PAYMENT_ID_2)
                .description(SURGERY_CHOLECYSTECTOMY)
                .performedDate(DATE_2024_04_10)
                .employeeId(SPECIALIST_ID_20)
                .fullName(SPECIALIST_NAME_STRANGE)
                .Cui(SPECIALIST_CUI_STRANGE)
                .build();

        willDoNothing().given(specialistPaymentService).createPaymentPerSurgery(paymentRequest);

        // when
        ResultActions result = mockMvc.perform(post(BASE_PATH)
                .contextPath(API_CONTEXT_PATH)
                .with(csrf())
                .with(jwt(SPECIALIST_ID_1, ROLE_ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentRequest)));

        // then
        result.andExpect(status().isCreated());

        thenMock(specialistPaymentService).should().createPaymentPerSurgery(paymentRequest);
    }
}
