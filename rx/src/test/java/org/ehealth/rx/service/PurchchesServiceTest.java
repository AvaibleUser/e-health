package org.ehealth.rx.service;

import org.ehealth.rx.domain.dto.CreatePurchacheDto;
import org.ehealth.rx.domain.dto.report.MedicinePurchacheDto;
import org.ehealth.rx.domain.dto.report.ReportExpenseMedicinePurchacheDto;
import org.ehealth.rx.domain.entity.MedicineEntity;
import org.ehealth.rx.domain.entity.PurchacheEntity;
import org.ehealth.rx.domain.exception.BadRequestException;
import org.ehealth.rx.repository.MedicineRepository;
import org.ehealth.rx.repository.PurchachesRepository;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PurchchesServiceTest {

    @Mock
    private PurchachesRepository purchachesRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private  PurchchesService purchchesService;

    @BeforeEach
    void setUp() {

    }

    private static final Long VALID_MEDICINE_ID = 1L;
    private static final Long INVALID_MEDICINE_ID = 99L;
    private static final Integer VALID_QUANTITY = 5;
    private static final BigDecimal MEDICINE_UNIT_COST = BigDecimal.valueOf(10.50);
    private static final int INITIAL_STOCK = 20;
    private static final Long MEDICINE_ID_1 = 1L;
    private static final Long MEDICINE_ID_2 = 2L;
    private static final String MEDICINE_NAME_1 = "Medicine A";
    private static final String MEDICINE_NAME_2 = "Medicine B";
    private static final Long PURCHASE_ID_1 = 101L;
    private static final Long PURCHASE_ID_2 = 102L;
    private static final BigDecimal UNIT_COST_1 = BigDecimal.valueOf(10.50);
    private static final BigDecimal UNIT_COST_2 = BigDecimal.valueOf(20.75);
    private static final Integer QUANTITY_1 = 5;
    private static final Integer QUANTITY_2 = 3;
    private static final Instant PURCHASE_DATE = Instant.now();
    private static final LocalDate VALID_START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate VALID_END_DATE = LocalDate.of(2023, 12, 31);
    private static final LocalDate NULL_DATE = null;


    /**
     * tests function create
     */

    @Test
    void shouldCreatePurchaseAndUpdateStockWhenMedicineExists() {
        // given
        MedicineEntity existingMedicine = buildMedicineEntity(VALID_MEDICINE_ID, INITIAL_STOCK);
        CreatePurchacheDto validPurchaseDto = buildCreatePurchacheDto(VALID_QUANTITY);

        given(medicineRepository.findById(VALID_MEDICINE_ID))
                .willReturn(Optional.of(existingMedicine));
        given(medicineRepository.save(any(MedicineEntity.class)))
                .willReturn(existingMedicine);
        given(purchachesRepository.save(any(PurchacheEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        purchchesService.create(VALID_MEDICINE_ID, validPurchaseDto);

        // then
        then(medicineRepository).should().findById(VALID_MEDICINE_ID);
        then(medicineRepository).should().save(argThat(medicine ->
                medicine.getStock() == INITIAL_STOCK + VALID_QUANTITY));
        then(purchachesRepository).should().save(argThat(purchase ->
                purchase.getMedicine().equals(existingMedicine) &&
                        purchase.getQuantity().equals(VALID_QUANTITY) &&
                        purchase.getUnitCost().equals(MEDICINE_UNIT_COST)));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenMedicineDoesNotExist() {
        // given
        CreatePurchacheDto validPurchaseDto = buildCreatePurchacheDto(VALID_QUANTITY);

        given(medicineRepository.findById(INVALID_MEDICINE_ID))
                .willReturn(Optional.empty());

        // when / then
        assertThrows(BadRequestException.class, () ->
                purchchesService.create(INVALID_MEDICINE_ID, validPurchaseDto));

        then(medicineRepository).should().findById(INVALID_MEDICINE_ID);
        then(medicineRepository).shouldHaveNoMoreInteractions();
        then(purchachesRepository).shouldHaveNoInteractions();
    }

    @Test
    void shouldPersistPurchaseWithCorrectValuesWhenValidInput() {
        // given
        MedicineEntity existingMedicine = buildMedicineEntity(VALID_MEDICINE_ID, INITIAL_STOCK);
        CreatePurchacheDto validPurchaseDto = buildCreatePurchacheDto(VALID_QUANTITY);

        given(medicineRepository.findById(VALID_MEDICINE_ID))
                .willReturn(Optional.of(existingMedicine));
        given(purchachesRepository.save(any(PurchacheEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        purchchesService.create(VALID_MEDICINE_ID, validPurchaseDto);

        // then
        then(purchachesRepository).should().save(argThat(purchase ->
                purchase.getMedicine().getId().equals(VALID_MEDICINE_ID) &&
                        purchase.getQuantity().equals(VALID_QUANTITY) &&
                        purchase.getUnitCost().equals(existingMedicine.getUnitCost())));
    }

    @Test
    void shouldUpdateMedicineStockCorrectlyWhenPurchaseIsCreated() {
        // given
        MedicineEntity existingMedicine = buildMedicineEntity(VALID_MEDICINE_ID, INITIAL_STOCK);
        CreatePurchacheDto validPurchaseDto = buildCreatePurchacheDto(VALID_QUANTITY);
        int expectedStock = INITIAL_STOCK + VALID_QUANTITY;

        given(medicineRepository.findById(VALID_MEDICINE_ID))
                .willReturn(Optional.of(existingMedicine));
        given(medicineRepository.save(any(MedicineEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        purchchesService.create(VALID_MEDICINE_ID, validPurchaseDto);

        // then
        then(medicineRepository).should().save(argThat(medicine ->
                medicine.getStock() == expectedStock));
    }

    /**
     * tests function getReportExpensePurchasesMedicine
     */
    @Test
    void shouldReturnEmptyReportWhenInputListIsNull() {
        // when
        ReportExpenseMedicinePurchacheDto result = purchchesService.getReportExpensePurchasesMedicine(null);

        // then
        assertThat(result.amountExpense()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.items()).isEmpty();
    }

    @Test
    void shouldReturnEmptyReportWhenInputListIsEmpty() {
        // when
        ReportExpenseMedicinePurchacheDto result = purchchesService.getReportExpensePurchasesMedicine(Collections.emptyList());

        // then
        assertThat(result.amountExpense()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.items()).isEmpty();
    }

    @Test
    void shouldCalculateCorrectTotalWithSingleItem() {
        // given
        MedicinePurchacheDto singleItem = buildMedicinePurchaseDto(
                MEDICINE_ID_1, MEDICINE_NAME_1, PURCHASE_ID_1, QUANTITY_1, UNIT_COST_1);
        List<MedicinePurchacheDto> items = List.of(singleItem);
        BigDecimal expectedTotal = UNIT_COST_1.multiply(BigDecimal.valueOf(QUANTITY_1));

        // when
        ReportExpenseMedicinePurchacheDto result = purchchesService.getReportExpensePurchasesMedicine(items);

        // then
        assertThat(result.amountExpense()).isEqualByComparingTo(expectedTotal);
        assertThat(result.items()).hasSize(1).containsExactly(singleItem);
    }

    @Test
    void shouldCalculateCorrectTotalWithMultipleItems() {
        // given
        MedicinePurchacheDto item1 = buildMedicinePurchaseDto(
                MEDICINE_ID_1, MEDICINE_NAME_1, PURCHASE_ID_1, QUANTITY_1, UNIT_COST_1);
        MedicinePurchacheDto item2 = buildMedicinePurchaseDto(
                MEDICINE_ID_2, MEDICINE_NAME_2, PURCHASE_ID_2, QUANTITY_2, UNIT_COST_2);
        List<MedicinePurchacheDto> items = List.of(item1, item2);

        BigDecimal expectedTotal = UNIT_COST_1.multiply(BigDecimal.valueOf(QUANTITY_1))
                .add(UNIT_COST_2.multiply(BigDecimal.valueOf(QUANTITY_2)));

        // when
        ReportExpenseMedicinePurchacheDto result = purchchesService.getReportExpensePurchasesMedicine(items);

        // then
        assertThat(result.amountExpense()).isEqualByComparingTo(expectedTotal);
        assertThat(result.items()).hasSize(2).containsExactly(item1, item2);
    }

    @Test
    void shouldPreserveInputOrderInResultItems() {
        // given
        MedicinePurchacheDto item1 = buildMedicinePurchaseDto(
                MEDICINE_ID_1, MEDICINE_NAME_1, PURCHASE_ID_1, QUANTITY_1, UNIT_COST_1);
        MedicinePurchacheDto item2 = buildMedicinePurchaseDto(
                MEDICINE_ID_2, MEDICINE_NAME_2, PURCHASE_ID_2, QUANTITY_2, UNIT_COST_2);
        List<MedicinePurchacheDto> items = List.of(item1, item2);

        // when
        ReportExpenseMedicinePurchacheDto result = purchchesService.getReportExpensePurchasesMedicine(items);

        // then
        assertThat(result.items()).containsExactlyElementsOf(items);
    }

    /**
     * tests function getReportExpensePurchasesMedicineInRange
     */
    @Test
    void shouldReturnReportWithAllPurchasesWhenDatesAreNull() {
        // given
        MedicinePurchacheDto item1 = buildMedicinePurchaseDto(MEDICINE_ID_1, MEDICINE_NAME_1,
                PURCHASE_ID_1, QUANTITY_1, UNIT_COST_1);
        MedicinePurchacheDto item2 = buildMedicinePurchaseDto(MEDICINE_ID_2, MEDICINE_NAME_2,
                PURCHASE_ID_2, QUANTITY_2, UNIT_COST_2);
        List<MedicinePurchacheDto> allItems = List.of(item1, item2);

        given(purchachesRepository.findAllPurchasesWithMedicine()).willReturn(allItems);

        // when
        ReportExpenseMedicinePurchacheDto result =
                purchchesService.getReportExpensePurchasesMedicineInRange(NULL_DATE, NULL_DATE);

        // then
        then(purchachesRepository).should().findAllPurchasesWithMedicine();
        then(purchachesRepository).shouldHaveNoMoreInteractions();

        assertThat(result.items()).containsExactlyElementsOf(allItems);
        assertThat(result.amountExpense()).isEqualByComparingTo(
                UNIT_COST_1.multiply(BigDecimal.valueOf(QUANTITY_1))
                        .add(UNIT_COST_2.multiply(BigDecimal.valueOf(QUANTITY_2)))
        );
    }


    @Test
    void shouldReturnEmptyReportWhenNoPurchasesFoundInDateRange() {
        // given
        given(purchachesRepository.findAllPurchasesWithMedicineInRange(any(), any()))
                .willReturn(Collections.emptyList());

        // when
        ReportExpenseMedicinePurchacheDto result =
                purchchesService.getReportExpensePurchasesMedicineInRange(VALID_START_DATE, VALID_END_DATE);

        // then
        assertThat(result.items()).isEmpty();
        assertThat(result.amountExpense()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldHandleStartDateNullAndEndDateValid() {
        // given
        MedicinePurchacheDto item = buildMedicinePurchaseDto(MEDICINE_ID_1, MEDICINE_NAME_1,
                PURCHASE_ID_1, QUANTITY_1, UNIT_COST_1);
        given(purchachesRepository.findAllPurchasesWithMedicine()).willReturn(List.of(item));

        // when
        ReportExpenseMedicinePurchacheDto result =
                purchchesService.getReportExpensePurchasesMedicineInRange(NULL_DATE, VALID_END_DATE);

        // then
        then(purchachesRepository).should().findAllPurchasesWithMedicine();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    void shouldHandleStartDateValidAndEndDateNull() {
        // given
        MedicinePurchacheDto item = buildMedicinePurchaseDto(MEDICINE_ID_1, MEDICINE_NAME_1,
                PURCHASE_ID_1, QUANTITY_1, UNIT_COST_1);
        given(purchachesRepository.findAllPurchasesWithMedicine()).willReturn(List.of(item));

        // when
        ReportExpenseMedicinePurchacheDto result =
                purchchesService.getReportExpensePurchasesMedicineInRange(VALID_START_DATE, NULL_DATE);

        // then
        then(purchachesRepository).should().findAllPurchasesWithMedicine();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    void shouldConvertDatesToInstantsUsingSystemDefaultZone() {
        // given
        ZoneId systemZone = ZoneId.systemDefault();
        Instant expectedStart = VALID_START_DATE.atStartOfDay(systemZone).toInstant();
        Instant expectedEnd = VALID_END_DATE.atStartOfDay(systemZone).toInstant();

        given(purchachesRepository.findAllPurchasesWithMedicineInRange(expectedStart, expectedEnd))
                .willReturn(Collections.emptyList());

        // when
        purchchesService.getReportExpensePurchasesMedicineInRange(VALID_START_DATE, VALID_END_DATE);

        // then
        then(purchachesRepository).should().findAllPurchasesWithMedicineInRange(expectedStart, expectedEnd);
    }

    private MedicineEntity buildMedicineEntity(Long id, int stock) {
        return MedicineEntity.builder()
                .id(id)
                .name("Test Medicine")
                .unitPrice(BigDecimal.valueOf(15.75))
                .unitCost(MEDICINE_UNIT_COST)
                .stock(stock)
                .minStock(10)
                .build();
    }

    private CreatePurchacheDto buildCreatePurchacheDto(Integer quantity) {
        return CreatePurchacheDto.builder()
                .quantity(quantity)
                .build();
    }

    private MedicinePurchacheDto buildMedicinePurchaseDto(Long medicineId, String name, Long purchaseId,
                                                          Integer quantity, BigDecimal unitCost) {
        return MedicinePurchacheDto.builder()
                .medicineId(medicineId)
                .name(name)
                .purchaseId(purchaseId)
                .quantity(quantity)
                .unitCost(unitCost)
                .purchasedAt(PURCHASE_DATE)
                .build();
    }


}