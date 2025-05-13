package org.ehealth.rx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.rx.domain.dto.CreateMedicineDto;
import org.ehealth.rx.domain.dto.MedicineDto;
import org.ehealth.rx.service.MedicineService;
import org.ehealth.rx.util.ControllerTest;
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

import static org.ehealth.rx.util.JwtBuilder.jwt;
import static org.ehealth.rx.util.ThenMockAlias.thenMock;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(classes = MedicineController.class, controllers = MedicineController.class)
class MedicineControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected MedicineService medicineService;

    private static final Long VALID_MEDICINE_ID = 1L;
    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-01T00:00:00Z");


    @Test
    void createMedicine_shouldReturnCreated() throws Exception {
        // given
        CreateMedicineDto dto = CreateMedicineDto.builder()
                .name("Paracetamol")
                .unitPrice(new BigDecimal("10.00"))
                .unitCost(new BigDecimal("5.00"))
                .stock(50)
                .minStock(10)
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/rx/v1/medicines")
                .contextPath("/api/rx")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .with(jwt(1L, "ADMIN")));

        // then
        result.andExpect(status().isCreated());

        thenMock(medicineService).should().create(dto);
    }

    @Test
    void findAllMedicines_shouldReturnList() throws Exception {
        // given
        List<MedicineDto> medicines = List.of(
                MedicineDto.builder()
                        .id(1L)
                        .name("Paracetamol")
                        .unitPrice(new BigDecimal("10.00"))
                        .unitCost(new BigDecimal("5.00"))
                        .stock(100)
                        .minStock(20)
                        .createdAt(FIXED_INSTANT)
                        .updatedAt(FIXED_INSTANT)
                        .build(),
                MedicineDto.builder()
                        .id(2L)
                        .name("Ibuprofeno")
                        .unitPrice(new BigDecimal("12.50"))
                        .unitCost(new BigDecimal("6.00"))
                        .stock(80)
                        .minStock(15)
                        .createdAt(FIXED_INSTANT)
                        .updatedAt(FIXED_INSTANT)
                        .build()
        );

        given(medicineService.findAll()).willReturn(medicines);

        // when
        ResultActions result = mockMvc.perform(get("/api/rx/v1/medicines")
                .contextPath("/api/rx")
                .with(jwt(1L, "ADMIN")));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(medicines)));

        thenMock(medicineService).should().findAll();
    }

    @Test
    void findMedicineById_shouldReturnMedicine() throws Exception {
        // given
        MedicineDto medicine = MedicineDto.builder()
                .id(VALID_MEDICINE_ID)
                .name("Paracetamol")
                .unitPrice(new BigDecimal("10.00"))
                .unitCost(new BigDecimal("5.00"))
                .stock(100)
                .minStock(20)
                .createdAt(FIXED_INSTANT)
                .updatedAt(FIXED_INSTANT)
                .build();

        given(medicineService.findById(VALID_MEDICINE_ID)).willReturn(medicine);

        // when
        ResultActions result = mockMvc.perform(get("/api/rx/v1/medicines/{id}", VALID_MEDICINE_ID)
                .contextPath("/api/rx")
                .with(jwt(1L, "ADMIN")));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(medicine)));

        thenMock(medicineService).should().findById(VALID_MEDICINE_ID);
    }

    @Test
    void updateMedicine_shouldReturnOk() throws Exception {
        // given
        CreateMedicineDto dto = CreateMedicineDto.builder()
                .name("Paracetamol Updated")
                .unitPrice(new BigDecimal("11.00"))
                .unitCost(new BigDecimal("5.50"))
                .stock(90)
                .minStock(15)
                .build();

        // when
        ResultActions result = mockMvc.perform(put("/api/rx/v1/medicines/{id}", VALID_MEDICINE_ID)
                .contextPath("/api/rx")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .with(jwt(1L, "ADMIN")));

        // then
        result.andExpect(status().isOk());

        thenMock(medicineService).should().update(VALID_MEDICINE_ID, dto);
    }


}