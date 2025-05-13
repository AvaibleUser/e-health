package org.ehealth.rx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.rx.domain.dto.CreatePurchacheDto;
import org.ehealth.rx.domain.dto.report.MedicinePurchacheDto;
import org.ehealth.rx.domain.dto.report.ReportExpenseMedicinePurchacheDto;
import org.ehealth.rx.service.PurchchesService;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(classes = PurchachesController.class, controllers = PurchachesController.class)
class PurchachesControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected PurchchesService purchesService;

    private static final Long MEDICINE_ID = 1L;

    private static final LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2024, 12, 31);
    private static final Instant FIXED_INSTANT = Instant.parse("2024-06-01T10:00:00Z");

    @Test
    void createPurchache_shouldReturnCreated() throws Exception {
        // given
        CreatePurchacheDto dto = CreatePurchacheDto.builder()
                .quantity(10)
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/rx/v1/purchaches/{medicineId}", MEDICINE_ID)
                .contextPath("/api/rx")
                .with(csrf())
                .with(jwt(1L, "ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isCreated());

        thenMock(purchesService).should().create(MEDICINE_ID, dto);
    }

    @Test
    void getReportExpensePurchaches_shouldReturnReport() throws Exception {
        // given
        ReportExpenseMedicinePurchacheDto report = ReportExpenseMedicinePurchacheDto.builder()
                .amountExpense(new BigDecimal("150.00"))
                .items(List.of(
                        MedicinePurchacheDto.builder()
                                .medicineId(1L)
                                .name("Ibuprofeno")
                                .purchaseId(100L)
                                .quantity(5)
                                .unitCost(new BigDecimal("30.00"))
                                .purchasedAt(FIXED_INSTANT)
                                .build()
                ))
                .build();

        given(purchesService.getReportExpensePurchasesMedicineInRange(START_DATE, END_DATE))
                .willReturn(report);

        // when
        ResultActions result = mockMvc.perform(get("/api/rx/v1/purchaches/report/Expense")
                .contextPath("/api/rx")
                .param("startDate", START_DATE.toString())
                .param("endDate", END_DATE.toString())
                .with(jwt(1L, "ADMIN")));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(report)));

        thenMock(purchesService).should().getReportExpensePurchasesMedicineInRange(START_DATE, END_DATE);
    }


}