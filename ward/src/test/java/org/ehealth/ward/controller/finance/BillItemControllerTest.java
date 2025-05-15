package org.ehealth.ward.controller.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.ward.domain.dto.finance.report.BillItemReportDto;
import org.ehealth.ward.domain.dto.finance.report.ReportIncomeBill;
import org.ehealth.ward.domain.entity.finance.BillItemEntity;
import org.ehealth.ward.service.finance.BillItemService;
import org.ehealth.ward.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
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

@ControllerTest(classes = BillItemController.class, controllers = BillItemController.class)
class BillItemControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected BillItemService billItemService;

    // URL Constants
    private static final String BASE_URL = "/api/ward/v1/bill-items";
    private static final String REPORT_INCOME_ENDPOINT = "/report/income";
    private static final String API_CONTEXT_PATH = "/api/ward";

    // Date Constants
    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-01T00:00:00Z");
    private static final LocalDate START_DATE_2024 = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE_2024 = LocalDate.of(2024, 12, 31);

    // Bill Item Constants
    private static final Long BILL_ITEM_ID_1 = 1L;
    private static final Long BILL_ITEM_ID_2 = 2L;
    private static final String CONCEPT_HAIRCUT = "Haircut";
    private static final String CONCEPT_BEARD_TRIM = "Beard Trim";
    private static final BigDecimal HAIRCUT_AMOUNT = new BigDecimal("25.00");
    private static final BigDecimal BEARD_TRIM_AMOUNT = new BigDecimal("15.00");
    private static final BigDecimal TOTAL_INCOME = new BigDecimal("40.00");
    private static final BillItemEntity.BillItemType SURGERY_TYPE = BillItemEntity.BillItemType.SURGERY;

    // User Constants
    private static final Long ADMIN_USER_ID = 1L;
    private static final String ROLE_ADMIN = "ADMIN";

    @Test
    void getReportSalesTotalInRange_shouldReturnReport() throws Exception {
        // given
        BillItemReportDto haircutItem = BillItemReportDto.builder()
                .id(BILL_ITEM_ID_1)
                .concept(CONCEPT_HAIRCUT)
                .amount(HAIRCUT_AMOUNT)
                .type(SURGERY_TYPE)
                .createdAt(FIXED_INSTANT)
                .build();

        BillItemReportDto beardTrimItem = BillItemReportDto.builder()
                .id(BILL_ITEM_ID_2)
                .concept(CONCEPT_BEARD_TRIM)
                .amount(BEARD_TRIM_AMOUNT)
                .type(SURGERY_TYPE)
                .createdAt(FIXED_INSTANT)
                .build();

        List<BillItemReportDto> billItems = List.of(haircutItem, beardTrimItem);

        ReportIncomeBill expectedReport = ReportIncomeBill.builder()
                .totalIncome(TOTAL_INCOME)
                .items(billItems)
                .build();

        given(billItemService.getReportIncomeBillInRange(START_DATE_2024, END_DATE_2024))
                .willReturn(expectedReport);

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL + REPORT_INCOME_ENDPOINT)
                .contextPath(API_CONTEXT_PATH)
                .param("startDate", START_DATE_2024.toString())
                .param("endDate", END_DATE_2024.toString())
                .with(jwt(ADMIN_USER_ID, ROLE_ADMIN)));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedReport)));

        thenMock(billItemService).should().getReportIncomeBillInRange(START_DATE_2024, END_DATE_2024);
    }
}