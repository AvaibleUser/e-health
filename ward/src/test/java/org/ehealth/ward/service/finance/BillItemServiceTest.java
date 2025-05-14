package org.ehealth.ward.service.finance;

import org.ehealth.ward.domain.dto.finance.report.BillItemReportDto;
import org.ehealth.ward.domain.dto.finance.report.ReportIncomeBill;
import org.ehealth.ward.domain.entity.finance.BillItemEntity;
import org.ehealth.ward.repository.finance.BillItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class BillItemServiceTest {

    @Mock
    private BillItemRepository billItemRepository;

    @InjectMocks
    private BillItemService billItemService;

    private static final Long VALID_BILL_ITEM_ID = 1L;
    private static final String VALID_CONCEPT = "Service fee";
    private static final BigDecimal VALID_AMOUNT = new BigDecimal("100.50");
    private static final BillItemEntity.BillItemType VALID_TYPE = BillItemEntity.BillItemType.SURGERY;
    private static final Instant VALID_CREATED_AT = Instant.now();


    private static final LocalDate VALID_START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate VALID_END_DATE = LocalDate.of(2023, 12, 31);
    private static final LocalDate INVALID_DATE = null;

    @BeforeEach
    void setUp() {

    }

    /**
     * tests function getReportIncomeBill
     */
    @Test
    void shouldReturnEmptyReportWhenInputListIsNull() {
        // given
        List<BillItemReportDto> nullItems = null;

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBill(nullItems);

        // then
        assertThat(result.totalIncome()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.items()).isEmpty();
    }

    @Test
    void shouldReturnEmptyReportWhenInputListIsEmpty() {
        // given
        List<BillItemReportDto> emptyItems = Collections.emptyList();

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBill(emptyItems);

        // then
        assertThat(result.totalIncome()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.items()).isEmpty();
    }

    @Test
    void shouldCalculateTotalIncomeForSingleItem() {
        // given
        BillItemReportDto singleItem = buildBillItemReportDto(VALID_AMOUNT);
        List<BillItemReportDto> items = List.of(singleItem);

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBill(items);

        // then
        assertThat(result.totalIncome()).isEqualTo(VALID_AMOUNT);
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0)).isEqualTo(singleItem);
    }

    @Test
    void shouldCalculateTotalIncomeForMultipleItems() {
        // given
        BigDecimal amount1 = new BigDecimal("150.75");
        BigDecimal amount2 = new BigDecimal("200.25");
        BigDecimal expectedTotal = amount1.add(amount2);

        BillItemReportDto item1 = buildBillItemReportDto(amount1);
        BillItemReportDto item2 = buildBillItemReportDto(amount2);
        List<BillItemReportDto> items = List.of(item1, item2);

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBill(items);

        // then
        assertThat(result.totalIncome()).isEqualByComparingTo(expectedTotal);
        assertThat(result.items()).hasSize(2);
        assertThat(result.items()).containsExactly(item1, item2);
    }

    @Test
    void shouldHandleZeroAmountItems() {
        // given
        BigDecimal zeroAmount = BigDecimal.ZERO;
        BillItemReportDto zeroItem = buildBillItemReportDto(zeroAmount);
        List<BillItemReportDto> items = List.of(zeroItem);

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBill(items);

        // then
        assertThat(result.totalIncome()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.items()).hasSize(1);
    }

    @Test
    void shouldHandleNegativeAmountItems() {
        // given
        BigDecimal negativeAmount = new BigDecimal("-50.25");
        BillItemReportDto negativeItem = buildBillItemReportDto(negativeAmount);
        List<BillItemReportDto> items = List.of(negativeItem);

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBill(items);

        // then
        assertThat(result.totalIncome()).isEqualByComparingTo(negativeAmount);
        assertThat(result.items()).hasSize(1);
    }

    @Test
    void shouldPreserveAllItemDetailsInReport() {
        // given
        BillItemReportDto detailedItem = BillItemReportDto.builder()
                .id(VALID_BILL_ITEM_ID)
                .concept(VALID_CONCEPT)
                .amount(VALID_AMOUNT)
                .type(VALID_TYPE)
                .createdAt(VALID_CREATED_AT)
                .build();
        List<BillItemReportDto> items = List.of(detailedItem);

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBill(items);

        // then
        assertThat(result.items().get(0).id()).isEqualTo(VALID_BILL_ITEM_ID);
        assertThat(result.items().get(0).concept()).isEqualTo(VALID_CONCEPT);
        assertThat(result.items().get(0).amount()).isEqualTo(VALID_AMOUNT);
        assertThat(result.items().get(0).type()).isEqualTo(VALID_TYPE);
        assertThat(result.items().get(0).createdAt()).isEqualTo(VALID_CREATED_AT);
    }


    /**
     * tests function getReportIncomeBillInRange
     */

    @Test
    void shouldReturnReportForAllItemsWhenDatesAreNull() {
        // given
        List<BillItemReportDto> expectedItems = List.of(buildBillItemReportDto(VALID_AMOUNT));
        given(billItemRepository.findAllByOrderByCreatedAtDesc(BillItemReportDto.class)).willReturn(expectedItems);

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBillInRange(INVALID_DATE, INVALID_DATE);

        // then
        assertThat(result.items()).isEqualTo(expectedItems);
        verify(billItemRepository).findAllByOrderByCreatedAtDesc(BillItemReportDto.class);
        verifyNoMoreInteractions(billItemRepository);
    }

    @Test
    void shouldReturnReportForAllItemsWhenStartDateIsNull() {
        // given
        List<BillItemReportDto> expectedItems = List.of(buildBillItemReportDto(VALID_AMOUNT));
        given(billItemRepository.findAllByOrderByCreatedAtDesc(BillItemReportDto.class)).willReturn(expectedItems);

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBillInRange(INVALID_DATE, VALID_END_DATE);

        // then
        assertThat(result.items()).isEqualTo(expectedItems);
        verify(billItemRepository).findAllByOrderByCreatedAtDesc(BillItemReportDto.class);
        verifyNoMoreInteractions(billItemRepository);
    }

    @Test
    void shouldReturnReportForAllItemsWhenEndDateIsNull() {
        // given
        List<BillItemReportDto> expectedItems = List.of(buildBillItemReportDto(VALID_AMOUNT));
        given(billItemRepository.findAllByOrderByCreatedAtDesc(BillItemReportDto.class)).willReturn(expectedItems);

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBillInRange(VALID_START_DATE, INVALID_DATE);

        // then
        assertThat(result.items()).isEqualTo(expectedItems);
        verify(billItemRepository).findAllByOrderByCreatedAtDesc(BillItemReportDto.class);
        verifyNoMoreInteractions(billItemRepository);
    }

    @Test
    void shouldReturnReportForItemsInDateRange() {
        // given
        Instant startInstant = VALID_START_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = VALID_END_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<BillItemReportDto> expectedItems = List.of(buildBillItemReportDto(VALID_AMOUNT));
        given(billItemRepository.findAllByCreatedAtBetweenOrderByCreatedAtDesc(
                startInstant, endInstant, BillItemReportDto.class))
                .willReturn(expectedItems);

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBillInRange(VALID_START_DATE, VALID_END_DATE);

        // then
        assertThat(result.items()).isEqualTo(expectedItems);
        verify(billItemRepository).findAllByCreatedAtBetweenOrderByCreatedAtDesc(
                startInstant, endInstant, BillItemReportDto.class);
        verifyNoMoreInteractions(billItemRepository);
    }

    @Test
    void shouldReturnEmptyReportWhenNoItemsFoundInRange() {
        // given
        Instant startInstant = VALID_START_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = VALID_END_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();

        given(billItemRepository.findAllByCreatedAtBetweenOrderByCreatedAtDesc(
                startInstant, endInstant, BillItemReportDto.class))
                .willReturn(Collections.emptyList());

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBillInRange(VALID_START_DATE, VALID_END_DATE);

        // then
        assertThat(result.items()).isEmpty();
        assertThat(result.totalIncome()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldReturnReportWithCorrectTotalForItemsInRange() {
        // given
        Instant startInstant = VALID_START_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = VALID_END_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();

        BigDecimal amount1 = new BigDecimal("150.00");
        BigDecimal amount2 = new BigDecimal("250.00");
        BigDecimal expectedTotal = amount1.add(amount2);

        List<BillItemReportDto> itemsInRange = List.of(
                buildBillItemReportDto(amount1),
                buildBillItemReportDto(amount2)
        );

        given(billItemRepository.findAllByCreatedAtBetweenOrderByCreatedAtDesc(
                startInstant, endInstant, BillItemReportDto.class))
                .willReturn(itemsInRange);

        // when
        ReportIncomeBill result = billItemService.getReportIncomeBillInRange(VALID_START_DATE, VALID_END_DATE);

        // then
        assertThat(result.totalIncome()).isEqualByComparingTo(expectedTotal);
        assertThat(result.items()).hasSize(2);
    }


    private BillItemReportDto buildBillItemReportDto(BigDecimal amount) {
        return BillItemReportDto.builder()
                .id(VALID_BILL_ITEM_ID)
                .concept(VALID_CONCEPT)
                .amount(amount)
                .type(VALID_TYPE)
                .createdAt(VALID_CREATED_AT)
                .build();
    }

}