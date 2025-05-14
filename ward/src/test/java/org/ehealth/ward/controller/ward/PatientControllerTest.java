package org.ehealth.ward.controller.ward;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.ward.domain.dto.ward.patient.PatientDto;
import org.ehealth.ward.service.ward.PatientService;
import org.ehealth.ward.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.ehealth.ward.util.JwtBuilder.jwt;
import static org.ehealth.ward.util.ThenMockAlias.thenMock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(classes = PatientController.class, controllers = PatientController.class)
class PatientControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected PatientService patientService;

    // URL Constants
    private static final String BASE_URL = "/api/ward/v1/patients";
    private static final String ALL_PATIENTS_ENDPOINT = "/all";
    private static final String EXIST_ENDPOINT = "/exist";
    private static final String API_CONTEXT_PATH = "/api/ward";

    // Patient Constants
    private static final Long PATIENT_ID_1 = 1L;
    private static final Long PATIENT_ID_2 = 2L;
    private static final String PATIENT_NAME_JOHN = "John Doe";
    private static final String PATIENT_NAME_JANE = "Jane Smith";
    private static final String PATIENT_CUI_JOHN = "1234567890123";
    private static final String PATIENT_CUI_JANE = "9876543210987";
    private static final String PATIENT_PHONE_JOHN = "555-1234";
    private static final String PATIENT_PHONE_JANE = "555-5678";
    private static final String PATIENT_EMAIL_JOHN = "john@example.com";
    private static final String PATIENT_EMAIL_JANE = "jane@example.com";

    // Date Constants
    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-01T00:00:00Z");
    private static final LocalDate BIRTH_DATE_JOHN = LocalDate.of(1990, 1, 1);
    private static final LocalDate BIRTH_DATE_JANE = LocalDate.of(1985, 5, 10);

    // User Constants
    private static final Long ADMIN_USER_ID = 1L;
    private static final String ROLE_ADMIN = "ADMIN";

    @Test
    void getAllPatients_shouldReturnList() throws Exception {
        // given
        PatientDto johnDoe = PatientDto.builder()
                .id(PATIENT_ID_1)
                .fullName(PATIENT_NAME_JOHN)
                .cui(PATIENT_CUI_JOHN)
                .birthDate(BIRTH_DATE_JOHN)
                .phone(PATIENT_PHONE_JOHN)
                .email(PATIENT_EMAIL_JOHN)
                .createdAt(FIXED_INSTANT)
                .updatedAt(FIXED_INSTANT)
                .build();

        PatientDto janeSmith = PatientDto.builder()
                .id(PATIENT_ID_2)
                .fullName(PATIENT_NAME_JANE)
                .cui(PATIENT_CUI_JANE)
                .birthDate(BIRTH_DATE_JANE)
                .phone(PATIENT_PHONE_JANE)
                .email(PATIENT_EMAIL_JANE)
                .createdAt(FIXED_INSTANT)
                .updatedAt(FIXED_INSTANT)
                .build();

        List<PatientDto> expectedPatients = List.of(johnDoe, janeSmith);

        given(patientService.findAll()).willReturn(expectedPatients);

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL + ALL_PATIENTS_ENDPOINT)
                .contextPath(API_CONTEXT_PATH)
                .with(jwt(ADMIN_USER_ID, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedPatients)));

        thenMock(patientService).should().findAll();
    }

    @Test
    void existPatientById_shouldReturnTrue() throws Exception {
        // given
        given(patientService.existPatientById(PATIENT_ID_1)).willReturn(true);

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL + EXIST_ENDPOINT + "/{id}", PATIENT_ID_1)
                .contextPath(API_CONTEXT_PATH)
                .with(jwt(ADMIN_USER_ID, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("true"));

        thenMock(patientService).should().existPatientById(PATIENT_ID_1);
    }

    @Test
    void existPatientById_shouldReturnFalse() throws Exception {
        // given
        given(patientService.existPatientById(PATIENT_ID_1)).willReturn(false);

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL + EXIST_ENDPOINT + "/{id}", PATIENT_ID_1)
                .contextPath(API_CONTEXT_PATH)
                .with(jwt(ADMIN_USER_ID, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("false"));

        thenMock(patientService).should().existPatientById(PATIENT_ID_1);
    }
}