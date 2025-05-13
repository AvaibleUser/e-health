package org.ehealth.rx.service;

import feign.FeignException;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.ehealth.rx.client.EmployeeClient;
import org.ehealth.rx.client.PatientClient;
import org.ehealth.rx.domain.dto.CreateSaleDto;
import org.ehealth.rx.domain.dto.ItemSaleDto;
import org.ehealth.rx.domain.dto.employee.EmployeeDto;
import org.ehealth.rx.domain.dto.report.*;
import org.ehealth.rx.domain.entity.MedicineEntity;
import org.ehealth.rx.domain.entity.SaleEntity;
import org.ehealth.rx.domain.exception.BadRequestException;
import org.ehealth.rx.domain.exception.RequestConflictException;
import org.ehealth.rx.domain.exception.ValueNotFoundException;
import org.ehealth.rx.repository.MedicineRepository;
import org.ehealth.rx.repository.SaleRepository;
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
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private PatientClient patientClient;

    @Mock
    private EmployeeClient employeeClient;

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private SaleService saleService;

    private static final String VALID_CUI = "1234567890123";
    private static final String INVALID_CUI = "0000000000000";
    private static final Long VALID_PATIENT_ID = 1L;
    private static final Long INVALID_PATIENT_ID = 999L;
    private static final Long VALID_MEDICINE_ID = 1L;
    private static final Integer VALID_QUANTITY = 5;
    private static final Long MEDICINE_ID_1 = 1L;
    private static final Long MEDICINE_ID_2 = 2L;
    private static final Integer QUANTITY = 3;
    private static final Long INVALID_MEDICINE_ID = 999L;
    private static final String MEDICINE_NAME = "Paracetamol";
    private static final BigDecimal UNIT_PRICE = new BigDecimal("5.99");
    private static final BigDecimal UNIT_COST = new BigDecimal("3.50");
    private static final Long NON_EXISTENT_MEDICINE_ID = 999L;
    private static final String MEDICINE_NAME_1 = "Paracetamol";
    private static final String MEDICINE_NAME_2 = "Ibuprofeno";
    private static final Integer QUANTITY_1 = 5;
    private static final Integer QUANTITY_2 = 10;
    private static final Integer INSUFFICIENT_STOCK_QUANTITY = 100;
    private static final Long VALID_EMPLOYEE_ID = 101L;
    private static final BigDecimal UNIT_PRICE_1 = new BigDecimal("10.50");
    private static final BigDecimal UNIT_PRICE_2 = new BigDecimal("8.75");
    private static final BigDecimal UNIT_COST_1 = new BigDecimal("5.25");
    private static final BigDecimal UNIT_COST_2 = new BigDecimal("4.10");
    private static final Instant SOLD_AT = Instant.now();
    private static final LocalDate START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 12, 31);
    private static final Instant START_INSTANT = START_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
    private static final Instant END_INSTANT = END_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
    private static final Long EMPLOYEE_ID_1 = 101L;
    private static final Long EMPLOYEE_ID_2 = 102L;
    private static final String EMPLOYEE_NAME_1 = "John Doe";
    private static final String EMPLOYEE_NAME_2 = "Jane Smith";
    private static final String CUI_1 = "1234567890123";
    private static final String CUI_2 = "9876543210987";
    private static final LocalDate VALID_START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate VALID_END_DATE = LocalDate.of(2024, 12, 31);
    private static final SaleMedicineDto SAMPLE_SALE_MEDICINE = buildSampleSaleMedicineDto();
    private static final List<SaleMedicineDto> SALE_MEDICINE_LIST = List.of(SAMPLE_SALE_MEDICINE);



    @BeforeEach
    void setUp() {

    }

    /**
     * tests function validateEntities
     */
    @Test
    void shouldReturnEmployeeDtoWhenCuiAndPatientIdAreValid() {
        // given
        EmployeeDto validEmployee = buildValidEmployeeDto(VALID_CUI);
        given(patientClient.existSurge(VALID_PATIENT_ID)).willReturn(true);
        given(employeeClient.findEmployeeByCui(VALID_CUI)).willReturn(validEmployee);

        // when
        EmployeeDto result = saleService.validateEntities(VALID_CUI, VALID_PATIENT_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.cui()).isEqualTo(VALID_CUI);
        verify(patientClient).existSurge(VALID_PATIENT_ID);
        verify(employeeClient).findEmployeeByCui(VALID_CUI);
    }

    @Test
    void shouldThrowValueNotFoundExceptionWhenPatientDoesNotExist() {
        // given
        given(patientClient.existSurge(INVALID_PATIENT_ID)).willReturn(false);
        given(employeeClient.findEmployeeByCui(VALID_CUI)).willReturn(null); // evitar NullPointerException

        // when / then
        assertThatThrownBy(() -> saleService.validateEntities(VALID_CUI, INVALID_PATIENT_ID))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessage("El paciente no existe");

        verify(patientClient).existSurge(INVALID_PATIENT_ID);
        verify(employeeClient).findEmployeeByCui(VALID_CUI);
    }

    @Test
    void shouldThrowValueNotFoundExceptionWhenEmployeeDoesNotExist() {
        // given
        given(patientClient.existSurge(VALID_PATIENT_ID)).willReturn(true);
        given(employeeClient.findEmployeeByCui(INVALID_CUI)).willReturn(null);

        // when / then
        assertThatThrownBy(() -> saleService.validateEntities(INVALID_CUI, VALID_PATIENT_ID))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessage("El empleado no existe");

        verify(patientClient).existSurge(VALID_PATIENT_ID);
        verify(employeeClient).findEmployeeByCui(INVALID_CUI);
    }

    @Test
    void shouldThrowRequestConflictExceptionWhenFeignClientFails() {
        // given
        given(patientClient.existSurge(VALID_PATIENT_ID))
                .willThrow(FeignException.class);

        // when / then
        assertThatThrownBy(() -> saleService.validateEntities(VALID_CUI, VALID_PATIENT_ID))
                .isInstanceOf(RequestConflictException.class)
                .hasMessage("No se pudo validar al paciente o empleado, intente más tarde");

        verify(patientClient).existSurge(VALID_PATIENT_ID);
        verifyNoInteractions(employeeClient);
    }

    /**
     * tests function validateItemList
     */
    @Test
    void shouldNotThrowExceptionWhenItemListIsValid() {
        // given
        List<ItemSaleDto> validItems = List.of(buildValidItemSaleDto());

        // when / then
        assertThatCode(() -> saleService.validateItemList(validItems))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowBadRequestExceptionWhenItemListIsNull() {
        // given
        List<ItemSaleDto> nullItems = null;

        // when / then
        assertThatThrownBy(() -> saleService.validateItemList(nullItems))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Debe agregar al menos un producto para realizar la venta");
    }

    @Test
    void shouldThrowBadRequestExceptionWhenItemListIsEmpty() {
        // given
        List<ItemSaleDto> emptyItems = Collections.emptyList();

        // when / then
        assertThatThrownBy(() -> saleService.validateItemList(emptyItems))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Debe agregar al menos un producto para realizar la venta");
    }

    /**
     * tests function extractMedicineIds
     */
    @Test
    void shouldReturnEmptySetWhenItemListIsEmpty() {
        // given
        List<ItemSaleDto> emptyItems = Collections.emptyList();

        // when
        Set<Long> result = saleService.extractMedicineIds(emptyItems);

        // then
        AssertionsForInterfaceTypes.assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnSingleMedicineIdWhenListHasOneItem() {
        // given
        List<ItemSaleDto> singleItem = List.of(
                buildItemSaleDto(MEDICINE_ID_1, QUANTITY)
        );

        // when
        Set<Long> result = saleService.extractMedicineIds(singleItem);

        // then
        AssertionsForInterfaceTypes.assertThat(result)
                .hasSize(1)
                .containsExactly(MEDICINE_ID_1);
    }

    @Test
    void shouldReturnUniqueMedicineIdsWhenListHasDuplicates() {
        // given
        List<ItemSaleDto> itemsWithDuplicates = List.of(
                buildItemSaleDto(MEDICINE_ID_1, QUANTITY),
                buildItemSaleDto(MEDICINE_ID_1, QUANTITY + 1), // mismo medicineId, diferente cantidad
                buildItemSaleDto(MEDICINE_ID_2, QUANTITY)
        );

        // when
        Set<Long> result = saleService.extractMedicineIds(itemsWithDuplicates);

        // then
        AssertionsForInterfaceTypes.assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(MEDICINE_ID_1, MEDICINE_ID_2);
    }

    @Test
    void shouldReturnMultipleMedicineIdsWhenListHasDifferentItems() {
        // given
        List<ItemSaleDto> multipleItems = List.of(
                buildItemSaleDto(MEDICINE_ID_1, QUANTITY),
                buildItemSaleDto(MEDICINE_ID_2, QUANTITY)
        );

        // when
        Set<Long> result = saleService.extractMedicineIds(multipleItems);

        // then
        AssertionsForInterfaceTypes.assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(MEDICINE_ID_1, MEDICINE_ID_2);
    }

    /**
     * tests function loadMedicinesById
     */
    @Test
    void shouldReturnEmptyMapWhenMedicineIdsSetIsEmpty() {
        // given
        Set<Long> emptyIds = Collections.emptySet();
        given(medicineRepository.findAllById(emptyIds)).willReturn(Collections.emptyList());

        // when
        Map<Long, MedicineEntity> result = saleService.loadMedicinesById(emptyIds);

        // then
        AssertionsForInterfaceTypes.assertThat(result).isEmpty();
        verify(medicineRepository).findAllById(emptyIds);
    }

    @Test
    void shouldReturnMedicinesMapWhenIdsExist() {
        // given
        Set<Long> medicineIds = Set.of(MEDICINE_ID_1, MEDICINE_ID_2);
        List<MedicineEntity> medicines = List.of(
                buildMedicineEntity(MEDICINE_ID_1),
                buildMedicineEntity(MEDICINE_ID_2)
        );

        given(medicineRepository.findAllById(medicineIds)).willReturn(medicines);

        // when
        Map<Long, MedicineEntity> result = saleService.loadMedicinesById(medicineIds);

        // then
        AssertionsForInterfaceTypes.assertThat(result)
                .hasSize(2)
                .containsKeys(MEDICINE_ID_1, MEDICINE_ID_2);

        assertThat(result.get(MEDICINE_ID_1).getId()).isEqualTo(MEDICINE_ID_1);
        assertThat(result.get(MEDICINE_ID_2).getId()).isEqualTo(MEDICINE_ID_2);
        verify(medicineRepository).findAllById(medicineIds);
    }

    @Test
    void shouldReturnPartialMapWhenSomeIdsDontExist() {
        // given
        Set<Long> medicineIds = Set.of(MEDICINE_ID_1, INVALID_MEDICINE_ID);
        List<MedicineEntity> medicines = List.of(
                buildMedicineEntity(MEDICINE_ID_1)
        );

        given(medicineRepository.findAllById(medicineIds)).willReturn(medicines);

        // when
        Map<Long, MedicineEntity> result = saleService.loadMedicinesById(medicineIds);

        // then
        AssertionsForInterfaceTypes.assertThat(result)
                .hasSize(1)
                .containsOnlyKeys(MEDICINE_ID_1);
        verify(medicineRepository).findAllById(medicineIds);
    }

    @Test
    void shouldReturnMapWithCorrectMedicineProperties() {
        // given
        Set<Long> medicineIds = Set.of(MEDICINE_ID_1);
        MedicineEntity medicine = buildMedicineEntity(MEDICINE_ID_1);
        given(medicineRepository.findAllById(medicineIds)).willReturn(List.of(medicine));

        // when
        Map<Long, MedicineEntity> result = saleService.loadMedicinesById(medicineIds);

        // then
        MedicineEntity resultMedicine = result.get(MEDICINE_ID_1);
        assertThat(resultMedicine.getName()).isEqualTo(MEDICINE_NAME);
        assertThat(resultMedicine.getUnitPrice()).isEqualTo(UNIT_PRICE);
        assertThat(resultMedicine.getUnitCost()).isEqualTo(UNIT_COST);
        verify(medicineRepository).findAllById(medicineIds);
    }

    /**
     * tests function validateStockAvailability
     */
    @Test
    void shouldNotThrowExceptionWhenStockIsSufficient() {
        // given
        Map<Long, MedicineEntity> medicineMap = Map.of(
                MEDICINE_ID_1, buildMedicineEntity(MEDICINE_ID_1, MEDICINE_NAME_1, 20),
                MEDICINE_ID_2, buildMedicineEntity(MEDICINE_ID_2, MEDICINE_NAME_2, 15)
        );

        List<ItemSaleDto> items = List.of(
                buildItemSaleDto(MEDICINE_ID_1, QUANTITY_1),
                buildItemSaleDto(MEDICINE_ID_2, QUANTITY_2)
        );

        // when / then
        assertThatCode(() -> saleService.validateStockAvailability(medicineMap, items))
                .doesNotThrowAnyException();

        assertThat(medicineMap.get(MEDICINE_ID_1).getStock()).isEqualTo(15); // 20 - 5
        assertThat(medicineMap.get(MEDICINE_ID_2).getStock()).isEqualTo(5);  // 15 - 10
    }

    @Test
    void shouldThrowRequestConflictExceptionWhenMedicineNotFound() {
        // given
        Map<Long, MedicineEntity> medicineMap = Map.of(
                MEDICINE_ID_1, buildMedicineEntity(MEDICINE_ID_1, MEDICINE_NAME_1, 20)
        );

        List<ItemSaleDto> items = List.of(
                buildItemSaleDto(NON_EXISTENT_MEDICINE_ID, QUANTITY_1)
        );

        // when / then
        assertThatThrownBy(() -> saleService.validateStockAvailability(medicineMap, items))
                .isInstanceOf(RequestConflictException.class)
                .hasMessage("El medicamento con ID " + NON_EXISTENT_MEDICINE_ID + " no existe");
    }

    @Test
    void shouldThrowRequestConflictExceptionWhenStockInsufficient() {
        // given
        Map<Long, MedicineEntity> medicineMap = Map.of(
                MEDICINE_ID_1, buildMedicineEntity(MEDICINE_ID_1, MEDICINE_NAME_1, 20)
        );

        List<ItemSaleDto> items = List.of(
                buildItemSaleDto(MEDICINE_ID_1, INSUFFICIENT_STOCK_QUANTITY)
        );

        // when / then
        assertThatThrownBy(() -> saleService.validateStockAvailability(medicineMap, items))
                .isInstanceOf(RequestConflictException.class)
                .hasMessage("El stock de " + MEDICINE_NAME_1 + " es insuficiente");
    }

    @Test
    void shouldUpdateStockCorrectlyForMultipleItems() {
        // given
        Map<Long, MedicineEntity> medicineMap = new HashMap<>(Map.of(
                MEDICINE_ID_1, buildMedicineEntity(MEDICINE_ID_1, MEDICINE_NAME_1, 30),
                MEDICINE_ID_2, buildMedicineEntity(MEDICINE_ID_2, MEDICINE_NAME_2, 25)
        ));

        List<ItemSaleDto> items = List.of(
                buildItemSaleDto(MEDICINE_ID_1, QUANTITY_1),
                buildItemSaleDto(MEDICINE_ID_1, QUANTITY_1), // mismo medicamento, segunda venta
                buildItemSaleDto(MEDICINE_ID_2, QUANTITY_2)
        );

        // when
        saleService.validateStockAvailability(medicineMap, items);

        // then
        assertThat(medicineMap.get(MEDICINE_ID_1).getStock()).isEqualTo(20); // 30 - 5 - 5
        assertThat(medicineMap.get(MEDICINE_ID_2).getStock()).isEqualTo(15); // 25 - 10
    }

    /**
     * tests function buildSales
     */
    @Test
    void shouldBuildSingleSaleEntityWhenSingleItemProvided() {
        // given
        MedicineEntity medicine = buildMedicineEntity(MEDICINE_ID_1, "Paracetamol", 100);
        Map<Long, MedicineEntity> medicineMap = Map.of(MEDICINE_ID_1, medicine);

        CreateSaleDto dto = CreateSaleDto.builder()
                .patientId(VALID_PATIENT_ID)
                .items(List.of(buildItemSaleDto(MEDICINE_ID_1, QUANTITY_1)))
                .build();

        // when
        List<SaleEntity> sales = saleService.buildSales(VALID_EMPLOYEE_ID, dto, medicineMap);

        // then
        AssertionsForInterfaceTypes.assertThat(sales).hasSize(1);

        SaleEntity sale = sales.get(0);
        assertThat(sale.getEmployeeId()).isEqualTo(VALID_EMPLOYEE_ID);
        assertThat(sale.getPatientId()).isEqualTo(VALID_PATIENT_ID);
        assertThat(sale.getMedicine()).isEqualTo(medicine);
        assertThat(sale.getQuantity()).isEqualTo(QUANTITY_1);
    }

    @Test
    void shouldBuildMultipleSaleEntitiesWhenMultipleItemsProvided() {
        // given
        MedicineEntity medicine1 = buildMedicineEntity(MEDICINE_ID_1, "Paracetamol", 100);
        MedicineEntity medicine2 = buildMedicineEntity(MEDICINE_ID_2, "Ibuprofeno", 50);

        Map<Long, MedicineEntity> medicineMap = Map.of(
                MEDICINE_ID_1, medicine1,
                MEDICINE_ID_2, medicine2
        );

        CreateSaleDto dto = CreateSaleDto.builder()
                .patientId(VALID_PATIENT_ID)
                .items(List.of(
                        buildItemSaleDto(MEDICINE_ID_1, QUANTITY_1),
                        buildItemSaleDto(MEDICINE_ID_2, QUANTITY_2)
                ))
                .build();

        // when
        List<SaleEntity> sales = saleService.buildSales(VALID_EMPLOYEE_ID, dto, medicineMap);

        // then
        AssertionsForInterfaceTypes.assertThat(sales).hasSize(2);

        // Verify first sale
        assertThat(sales.get(0).getMedicine()).isEqualTo(medicine1);
        assertThat(sales.get(0).getQuantity()).isEqualTo(QUANTITY_1);

        // Verify second sale
        assertThat(sales.get(1).getMedicine()).isEqualTo(medicine2);
        assertThat(sales.get(1).getQuantity()).isEqualTo(QUANTITY_2);
    }

    @Test
    void shouldBuildSalesWithCorrectUnitPriceFromMedicine() {
        // given
        BigDecimal specialPrice = new BigDecimal("12.50");
        MedicineEntity medicine = buildMedicineEntity(MEDICINE_ID_1, "Paracetamol", 100)
                .toBuilder()
                .unitPrice(specialPrice)
                .build();

        Map<Long, MedicineEntity> medicineMap = Map.of(MEDICINE_ID_1, medicine);

        CreateSaleDto dto = CreateSaleDto.builder()
                .patientId(VALID_PATIENT_ID)
                .items(List.of(buildItemSaleDto(MEDICINE_ID_1, QUANTITY_1)))
                .build();

        // when
        List<SaleEntity> sales = saleService.buildSales(VALID_EMPLOYEE_ID, dto, medicineMap);

        // then
        assertThat(sales.get(0).getUnitPrice()).isEqualTo(specialPrice);
    }

    @Test
    void shouldMaintainItemOrderInResultingSales() {
        // given
        MedicineEntity medicine1 = buildMedicineEntity(MEDICINE_ID_1, "Paracetamol", 100);
        MedicineEntity medicine2 = buildMedicineEntity(MEDICINE_ID_2, "Ibuprofeno", 50);

        Map<Long, MedicineEntity> medicineMap = Map.of(
                MEDICINE_ID_1, medicine1,
                MEDICINE_ID_2, medicine2
        );

        ItemSaleDto firstItem = buildItemSaleDto(MEDICINE_ID_1, QUANTITY_1);
        ItemSaleDto secondItem = buildItemSaleDto(MEDICINE_ID_2, QUANTITY_2);

        CreateSaleDto dto = CreateSaleDto.builder()
                .patientId(VALID_PATIENT_ID)
                .items(List.of(firstItem, secondItem))
                .build();

        // when
        List<SaleEntity> sales = saleService.buildSales(VALID_EMPLOYEE_ID, dto, medicineMap);

        // then
        assertThat(sales.get(0).getMedicine().getId()).isEqualTo(firstItem.medicineId());
        assertThat(sales.get(1).getMedicine().getId()).isEqualTo(secondItem.medicineId());
    }

    /**
     * tests function createSaleTotal
     */
    @Test
    void shouldCreateSaleSuccessfullyWhenAllValidationsPass() {
        // given
        CreateSaleDto validSaleDto = CreateSaleDto.builder()
                .patientId(VALID_PATIENT_ID)
                .items(List.of(
                        buildItemSaleDto(MEDICINE_ID_1, QUANTITY_1),
                        buildItemSaleDto(MEDICINE_ID_2, QUANTITY_2)
                ))
                .build();

        EmployeeDto validEmployee = EmployeeDto.builder()
                .id(VALID_EMPLOYEE_ID)
                .cui(VALID_CUI)
                .build();

        MedicineEntity medicine1 = buildMedicineEntity(MEDICINE_ID_1, "Paracetamol", 100);
        MedicineEntity medicine2 = buildMedicineEntity(MEDICINE_ID_2, "Ibuprofeno", 50);
        Map<Long, MedicineEntity> medicineMap = new HashMap<>(Map.of(
                MEDICINE_ID_1, medicine1,
                MEDICINE_ID_2, medicine2
        ));

        List<SaleEntity> expectedSales = List.of(
                SaleEntity.builder()
                        .employeeId(VALID_EMPLOYEE_ID)
                        .patientId(VALID_PATIENT_ID)
                        .medicine(medicine1)
                        .quantity(QUANTITY_1)
                        .unitPrice(medicine1.getUnitPrice())
                        .build(),
                SaleEntity.builder()
                        .employeeId(VALID_EMPLOYEE_ID)
                        .patientId(VALID_PATIENT_ID)
                        .medicine(medicine2)
                        .quantity(QUANTITY_2)
                        .unitPrice(medicine2.getUnitPrice())
                        .build()
        );

        given(patientClient.existSurge(VALID_PATIENT_ID)).willReturn(true);
        given(employeeClient.findEmployeeByCui(VALID_CUI)).willReturn(validEmployee);
        given(medicineRepository.findAllById(anySet())).willReturn(new ArrayList<>(medicineMap.values()));
        given(saleRepository.saveAll(anyList())).willReturn(expectedSales);

        // when
        saleService.createSaleTotal(VALID_CUI, validSaleDto);

        // then
        verify(patientClient).existSurge(VALID_PATIENT_ID);
        verify(employeeClient).findEmployeeByCui(VALID_CUI);

        assertThat(medicineMap.get(MEDICINE_ID_1).getStock()).isEqualTo(100 - QUANTITY_1);
        assertThat(medicineMap.get(MEDICINE_ID_2).getStock()).isEqualTo(50 - QUANTITY_2);


    }

    @Test
    void shouldThrowExceptionWhenPatientValidationFails() {
        // given
        CreateSaleDto invalidDto = CreateSaleDto.builder()
                .patientId(INVALID_PATIENT_ID)
                .items(List.of(buildItemSaleDto(MEDICINE_ID_1, QUANTITY_1)))
                .build();

        given(patientClient.existSurge(INVALID_PATIENT_ID)).willReturn(false);

        // when / then
        assertThatThrownBy(() -> saleService.createSaleTotal(VALID_CUI, invalidDto))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessage("El paciente no existe");

        verifyNoInteractions(medicineRepository, saleRepository);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeValidationFails() {
        // given
        CreateSaleDto validDto = CreateSaleDto.builder()
                .patientId(VALID_PATIENT_ID)
                .items(List.of(buildItemSaleDto(MEDICINE_ID_1, QUANTITY_1)))
                .build();

        given(patientClient.existSurge(VALID_PATIENT_ID)).willReturn(true);
        given(employeeClient.findEmployeeByCui(VALID_CUI)).willReturn(null);

        // when / then
        assertThatThrownBy(() -> saleService.createSaleTotal(VALID_CUI, validDto))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessage("El empleado no existe");

        verifyNoInteractions(medicineRepository, saleRepository);
    }

    @Test
    void shouldThrowExceptionWhenItemsListIsEmpty() {
        // given
        CreateSaleDto emptyItemsDto = CreateSaleDto.builder()
                .patientId(VALID_PATIENT_ID)
                .items(Collections.emptyList())
                .build();

        EmployeeDto validEmployee = buildValidEmployeeDto(VALID_CUI);
        given(patientClient.existSurge(VALID_PATIENT_ID)).willReturn(true);
        given(employeeClient.findEmployeeByCui(VALID_CUI)).willReturn(validEmployee);

        // when / then
        assertThatThrownBy(() -> saleService.createSaleTotal(VALID_CUI, emptyItemsDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Debe agregar al menos un producto para realizar la venta");

        verifyNoInteractions(medicineRepository, saleRepository);
    }

    @Test
    void shouldThrowExceptionWhenMedicineStockIsInsufficient() {
        // given
        CreateSaleDto validDto = CreateSaleDto.builder()
                .patientId(VALID_PATIENT_ID)
                .items(List.of(buildItemSaleDto(MEDICINE_ID_1, INSUFFICIENT_STOCK_QUANTITY)))
                .build();

        EmployeeDto validEmployee = buildValidEmployeeDto(VALID_CUI);
        MedicineEntity lowStockMedicine = buildMedicineEntity(MEDICINE_ID_1, "Paracetamol", 10);

        given(patientClient.existSurge(VALID_PATIENT_ID)).willReturn(true);
        given(employeeClient.findEmployeeByCui(VALID_CUI)).willReturn(validEmployee);
        given(medicineRepository.findAllById(anySet())).willReturn(List.of(lowStockMedicine));

        // when / then
        assertThatThrownBy(() -> saleService.createSaleTotal(VALID_CUI, validDto))
                .isInstanceOf(RequestConflictException.class)
                .hasMessage("El stock de Paracetamol es insuficiente");

        verify(medicineRepository, never()).saveAll(any());
    }

    /**
     * tests function getReportSalesMedicinePerMedicine
     */
    @Test
    void shouldGenerateEmptyReportWhenInputListIsEmpty() {
        // given
        List<SaleMedicineDto> emptyList = Collections.emptyList();

        // when
        List<ReportSaleMedicineDto> result = saleService.getReportSalesMedicinePerMedicine(emptyList);

        // then
        AssertionsForInterfaceTypes.assertThat(result).isEmpty();
    }

    @Test
    void shouldGenerateReportForSingleMedicineWithSingleSale() {
        // given
        SaleMedicineDto sale = buildSaleMedicineDto(
                MEDICINE_ID_1, MEDICINE_NAME_1,
                UNIT_PRICE_1, UNIT_COST_1,
                5, 1L, SOLD_AT
        );

        // when
        List<ReportSaleMedicineDto> result = saleService.getReportSalesMedicinePerMedicine(List.of(sale));

        // then
        AssertionsForInterfaceTypes.assertThat(result).hasSize(1);

        ReportSaleMedicineDto report = result.get(0);
        assertThat(report.medicineId()).isEqualTo(MEDICINE_ID_1);
        assertThat(report.name()).isEqualTo(MEDICINE_NAME_1);
        assertThat(report.totalSold()).isEqualTo(5);
        assertThat(report.totalIncome()).isEqualByComparingTo("52.50"); // 10.50 * 5
        assertThat(report.totalProfit()).isEqualByComparingTo("26.25"); // (10.50 - 5.25) * 5

        AssertionsForInterfaceTypes.assertThat(report.items()).hasSize(1);
        ItemsSaleMedicineDto item = report.items().get(0);
        assertThat(item.Subtotal()).isEqualByComparingTo("52.50");
        assertThat(item.Profit()).isEqualByComparingTo("26.25");
    }

    @Test
    void shouldGenerateReportForSingleMedicineWithMultipleSales() {
        // given
        List<SaleMedicineDto> sales = List.of(
                buildSaleMedicineDto(MEDICINE_ID_1, MEDICINE_NAME_1, UNIT_PRICE_1, UNIT_COST_1, 3, 1L, SOLD_AT),
                buildSaleMedicineDto(MEDICINE_ID_1, MEDICINE_NAME_1, UNIT_PRICE_1, UNIT_COST_1, 2, 2L, SOLD_AT.plusSeconds(3600))
        );

        // when
        List<ReportSaleMedicineDto> result = saleService.getReportSalesMedicinePerMedicine(sales);

        // then
        AssertionsForInterfaceTypes.assertThat(result).hasSize(1);

        ReportSaleMedicineDto report = result.get(0);
        assertThat(report.totalSold()).isEqualTo(5); // 3 + 2
        assertThat(report.totalIncome()).isEqualByComparingTo("52.50"); // 10.50 * 5
        assertThat(report.totalProfit()).isEqualByComparingTo("26.25"); // (10.50 - 5.25) * 5
        AssertionsForInterfaceTypes.assertThat(report.items()).hasSize(2);
    }

    @Test
    void shouldGenerateReportForMultipleMedicines() {
        // given
        List<SaleMedicineDto> sales = List.of(
                buildSaleMedicineDto(MEDICINE_ID_1, MEDICINE_NAME_1, UNIT_PRICE_1, UNIT_COST_1, 2, 1L, SOLD_AT),
                buildSaleMedicineDto(MEDICINE_ID_2, MEDICINE_NAME_2, UNIT_PRICE_2, UNIT_COST_2, 4, 2L, SOLD_AT)
        );

        // when
        List<ReportSaleMedicineDto> result = saleService.getReportSalesMedicinePerMedicine(sales);

        // then
        AssertionsForInterfaceTypes.assertThat(result).hasSize(2);

        // Verify Medicine 1
        ReportSaleMedicineDto report1 = result.stream()
                .filter(r -> r.medicineId().equals(MEDICINE_ID_1))
                .findFirst()
                .orElseThrow();

        assertThat(report1.totalSold()).isEqualTo(2);
        assertThat(report1.totalIncome()).isEqualByComparingTo("21.00"); // 10.50 * 2
        assertThat(report1.totalProfit()).isEqualByComparingTo("10.50"); // (10.50 - 5.25) * 2

        // Verify Medicine 2
        ReportSaleMedicineDto report2 = result.stream()
                .filter(r -> r.medicineId().equals(MEDICINE_ID_2))
                .findFirst()
                .orElseThrow();

        assertThat(report2.totalSold()).isEqualTo(4);
        assertThat(report2.totalIncome()).isEqualByComparingTo("35.00"); // 8.75 * 4
        assertThat(report2.totalProfit()).isEqualByComparingTo("18.60"); // (8.75 - 4.10) * 4
    }

    @Test
    void shouldCalculateCorrectProfitForEachItem() {
        // given
        List<SaleMedicineDto> sales = List.of(
                buildSaleMedicineDto(MEDICINE_ID_1, MEDICINE_NAME_1, UNIT_PRICE_1, UNIT_COST_1, 1, 1L, SOLD_AT),
                buildSaleMedicineDto(MEDICINE_ID_1, MEDICINE_NAME_1, UNIT_PRICE_1, UNIT_COST_1, 2, 2L, SOLD_AT)
        );

        // when
        List<ReportSaleMedicineDto> result = saleService.getReportSalesMedicinePerMedicine(sales);

        // then
        ItemsSaleMedicineDto item1 = result.get(0).items().get(0);
        assertThat(item1.Profit()).isEqualByComparingTo("5.25"); // (10.50 - 5.25) * 1

        ItemsSaleMedicineDto item2 = result.get(0).items().get(1);
        assertThat(item2.Profit()).isEqualByComparingTo("10.50"); // (10.50 - 5.25) * 2
    }

    @Test
    void shouldMaintainCorrectOrderOfItemsInReport() {
        // given
        Instant firstSaleTime = SOLD_AT;
        Instant secondSaleTime = SOLD_AT.plusSeconds(3600);

        List<SaleMedicineDto> sales = List.of(
                buildSaleMedicineDto(MEDICINE_ID_1, MEDICINE_NAME_1, UNIT_PRICE_1, UNIT_COST_1, 1, 2L, secondSaleTime),
                buildSaleMedicineDto(MEDICINE_ID_1, MEDICINE_NAME_1, UNIT_PRICE_1, UNIT_COST_1, 1, 1L, firstSaleTime)
        );

        // when
        List<ReportSaleMedicineDto> result = saleService.getReportSalesMedicinePerMedicine(sales);

        // then
        AssertionsForInterfaceTypes.assertThat(result.get(0).items())
                .extracting(ItemsSaleMedicineDto::saleId)
                .containsExactly( 2L,1L);
    }

    /**
     * tests function getReportSalesMedicinePerMedicineInRange
     */
    @Test
    void shouldReturnReportForAllSalesWhenDatesAreNull() {
        // given
        List<SaleMedicineDto> allSales = List.of(
                buildSaleMedicineDto(MEDICINE_ID_1, MEDICINE_NAME_1, UNIT_PRICE_1, UNIT_COST_1, 2, 1L, SOLD_AT),
                buildSaleMedicineDto(MEDICINE_ID_2, MEDICINE_NAME_2, UNIT_PRICE_2, UNIT_COST_2, 3, 2L, SOLD_AT)
        );

        given(saleRepository.findAllSalesWithMedicine()).willReturn(allSales);

        // when
        List<ReportSaleMedicineDto> result = saleService.getReportSalesMedicinePerMedicineInRange(START_DATE, null);

        // then
        AssertionsForInterfaceTypes.assertThat(result).hasSize(2);
        verify(saleRepository).findAllSalesWithMedicine();
        verify(saleRepository, never()).findSalesWithMedicineBetweenDates(any(), any());
    }

    @Test
    void shouldReturnReportForDateRangeWhenDatesAreProvided() {
        // given
        List<SaleMedicineDto> filteredSales = List.of(
                buildSaleMedicineDto(
                        MEDICINE_ID_1,
                        MEDICINE_NAME_1,
                        UNIT_PRICE_1,
                        UNIT_COST_1,
                        2,
                        1L,
                        START_INSTANT.plusSeconds(3600)
                )
        );

        given(saleRepository.findSalesWithMedicineBetweenDates(START_INSTANT, END_INSTANT))
                .willReturn(filteredSales);

        // when
        List<ReportSaleMedicineDto> result = saleService.getReportSalesMedicinePerMedicineInRange(START_DATE, END_DATE);

        // then
        AssertionsForInterfaceTypes.assertThat(result).hasSize(1);
        verify(saleRepository).findSalesWithMedicineBetweenDates(START_INSTANT, END_INSTANT);
        verify(saleRepository, never()).findAllSalesWithMedicine();
    }

    @Test
    void shouldReturnEmptyReportWhenNoSalesInDateRange() {
        // given
        given(saleRepository.findSalesWithMedicineBetweenDates(START_INSTANT, END_INSTANT)).willReturn(Collections.emptyList());

        // when
        List<ReportSaleMedicineDto> result = saleService.getReportSalesMedicinePerMedicineInRange(START_DATE, END_DATE);

        // then
        AssertionsForInterfaceTypes.assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyReportWhenNoSalesExist() {
        // given
        given(saleRepository.findAllSalesWithMedicine()).willReturn(Collections.emptyList());

        // when
        List<ReportSaleMedicineDto> result = saleService.getReportSalesMedicinePerMedicineInRange(null, null);

        // then
        AssertionsForInterfaceTypes.assertThat(result).isEmpty();
    }

    @Test
    void shouldConvertDatesToInstantsCorrectly() {
        // given
        LocalDate testStartDate = LocalDate.of(2023, 6, 1);
        LocalDate testEndDate = LocalDate.of(2023, 6, 30);
        Instant expectedStartInstant = testStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant expectedEndInstant = testEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        given(saleRepository.findSalesWithMedicineBetweenDates(expectedStartInstant, expectedEndInstant))
                .willReturn(Collections.emptyList());

        // when
        saleService.getReportSalesMedicinePerMedicineInRange(testStartDate, testEndDate);

        // then
        verify(saleRepository).findSalesWithMedicineBetweenDates(expectedStartInstant, expectedEndInstant);
    }

    @Test
    void shouldReuseExistingReportGenerationLogic() {
        // given
        List<SaleMedicineDto> testSales = List.of(
                buildSaleMedicineDto(MEDICINE_ID_1, MEDICINE_NAME_1, UNIT_PRICE_1, UNIT_COST_1, 2, 1L, SOLD_AT)
        );

        given(saleRepository.findAllSalesWithMedicine()).willReturn(testSales);

        // when
        List<ReportSaleMedicineDto> result = saleService.getReportSalesMedicinePerMedicineInRange(null, null);

        // then
        assertThat(result.get(0).medicineId()).isEqualTo(MEDICINE_ID_1);
        assertThat(result.get(0).totalSold()).isEqualTo(2);
    }

    /**
     * tests function getReportSalesMedicinePerEmployee
     */
    @Test
    void shouldGenerateReportGroupedByEmployee() {
        // given
        List<EmployeeDto> employees = List.of(
                buildEmployeeDto(EMPLOYEE_ID_1, EMPLOYEE_NAME_1, CUI_1),
                buildEmployeeDto(EMPLOYEE_ID_2, EMPLOYEE_NAME_2, CUI_2)
        );

        List<SaleMedicineDto> sales = List.of(
                buildSaleMedicineDtoWithEmployee(MEDICINE_ID_1, "Paracetamol", new BigDecimal("10.50"), new BigDecimal("5.25"),
                        2, 1L, SOLD_AT, EMPLOYEE_ID_1),
                buildSaleMedicineDtoWithEmployee(MEDICINE_ID_2, "Ibuprofeno", new BigDecimal("8.75"), new BigDecimal("4.10"),
                        3, 2L, SOLD_AT, EMPLOYEE_ID_1)
        );

        given(employeeClient.findAllEmployees()).willReturn(employees);

        // when
        List<ReportSalesPerEmployeeDto> result = saleService.getReportSalesMedicinePerEmployee(sales);

        // then
        AssertionsForInterfaceTypes.assertThat(result).hasSize(1);

        ReportSalesPerEmployeeDto report = result.get(0);
        assertThat(report.employeeId()).isEqualTo(EMPLOYEE_ID_1);
        assertThat(report.employeeName()).isEqualTo(EMPLOYEE_NAME_1);
        assertThat(report.cui()).isEqualTo(CUI_1);
        assertThat(report.totalSold()).isEqualTo(5); // 2 + 3

        assertThat(report.totalIncome()).isEqualByComparingTo("47.25");
        assertThat(report.totalProfit()).isEqualByComparingTo("24.45");
        AssertionsForInterfaceTypes.assertThat(report.items()).hasSize(2);
    }

    @Test
    void shouldReturnEmptyReportWhenNoEmployeeSales() {
        // given
        List<EmployeeDto> employees = List.of(
                buildEmployeeDto(EMPLOYEE_ID_1, EMPLOYEE_NAME_1, CUI_1)
        );

        given(employeeClient.findAllEmployees()).willReturn(employees);

        // when
        List<ReportSalesPerEmployeeDto> result = saleService.getReportSalesMedicinePerEmployee(Collections.emptyList());

        // then
        AssertionsForInterfaceTypes.assertThat(result).isEmpty();
    }

    @Test
    void shouldSkipEmployeesWithNoSales() {
        // given
        List<EmployeeDto> employees = List.of(
                buildEmployeeDto(EMPLOYEE_ID_1, EMPLOYEE_NAME_1, CUI_1),
                buildEmployeeDto(EMPLOYEE_ID_2, EMPLOYEE_NAME_2, CUI_2)
        );

        List<SaleMedicineDto> sales = List.of(
                buildSaleMedicineDtoWithEmployee(MEDICINE_ID_1, "Paracetamol", UNIT_PRICE_1, UNIT_COST_1,
                        2, 1L, SOLD_AT, EMPLOYEE_ID_1)
        );

        given(employeeClient.findAllEmployees()).willReturn(employees);

        // when
        List<ReportSalesPerEmployeeDto> result = saleService.getReportSalesMedicinePerEmployee(sales);

        // then
        AssertionsForInterfaceTypes.assertThat(result).hasSize(1);
        assertThat(result.get(0).employeeId()).isEqualTo(EMPLOYEE_ID_1);
    }

    @Test
    void shouldCalculateCorrectProfitPerItem() {
        // given
        List<EmployeeDto> employees = List.of(
                buildEmployeeDto(EMPLOYEE_ID_1, EMPLOYEE_NAME_1, CUI_1)
        );

        List<SaleMedicineDto> sales = List.of(
                buildSaleMedicineDtoWithEmployee(MEDICINE_ID_1, "Paracetamol", UNIT_PRICE_1, UNIT_COST_1,
                        1, 1L, SOLD_AT, EMPLOYEE_ID_1)
        );

        given(employeeClient.findAllEmployees()).willReturn(employees);

        // when
        List<ReportSalesPerEmployeeDto> result = saleService.getReportSalesMedicinePerEmployee(sales);

        // then
        ItemsSalePerEmployeeDto item = result.get(0).items().get(0);
        assertThat(item.Profit()).isEqualByComparingTo("5.25"); // 10.50 - 5.25
    }

    @Test
    void shouldThrowRequestConflictExceptionWhenEmployeeClientFails() {
        // given
        given(employeeClient.findAllEmployees()).willThrow(FeignException.class);

        // when / then
        assertThatThrownBy(() -> saleService.getReportSalesMedicinePerEmployee(Collections.emptyList()))
                .isInstanceOf(RequestConflictException.class)
                .hasMessage("No se obtener empleados para el reporte");
    }

    @Test
    void shouldIncludeAllRequiredEmployeeDataInReport() {
        // given
        List<EmployeeDto> employees = List.of(
                buildEmployeeDto(EMPLOYEE_ID_1, EMPLOYEE_NAME_1, CUI_1)
        );

        List<SaleMedicineDto> sales = List.of(
                buildSaleMedicineDtoWithEmployee(MEDICINE_ID_1, "Paracetamol", UNIT_PRICE_1, UNIT_COST_1,
                        1, 1L, SOLD_AT, EMPLOYEE_ID_1)
        );

        given(employeeClient.findAllEmployees()).willReturn(employees);

        // when
        List<ReportSalesPerEmployeeDto> result = saleService.getReportSalesMedicinePerEmployee(sales);

        // then
        ReportSalesPerEmployeeDto report = result.get(0);
        assertThat(report.employeeId()).isEqualTo(EMPLOYEE_ID_1);
        assertThat(report.employeeName()).isEqualTo(EMPLOYEE_NAME_1);
        assertThat(report.cui()).isEqualTo(CUI_1);
    }

    /**
     * tests function getReportSalesMedicineEmployeeInRange
     */
    @Test
    void shouldReturnAllSalesReportWhenDatesAreNull() {
        // given
        List<SaleMedicineDto> allSales = List.of(
                buildSaleMedicineDtoWithEmployee(MEDICINE_ID_1, "Paracetamol", UNIT_PRICE_1, UNIT_COST_1,
                        2, 1L, SOLD_AT, EMPLOYEE_ID_1)
        );

        List<EmployeeDto> employees = List.of(
                buildEmployeeDto(EMPLOYEE_ID_1, EMPLOYEE_NAME_1, CUI_1)
        );

        given(saleRepository.findAllSalesWithMedicine()).willReturn(allSales);
        given(employeeClient.findAllEmployees()).willReturn(employees);

        // when
        List<ReportSalesPerEmployeeDto> result = saleService.getReportSalesMedicineEmployeeInRange(START_DATE, null);

        // then
        AssertionsForInterfaceTypes.assertThat(result).hasSize(1);
        verify(saleRepository).findAllSalesWithMedicine();
        verify(saleRepository, never()).findSalesWithMedicineBetweenDates(any(), any());
    }

    @Test
    void shouldReturnFilteredSalesReportWhenDatesAreProvided() {
        // given
        List<SaleMedicineDto> filteredSales = List.of(
                buildSaleMedicineDtoWithEmployee(MEDICINE_ID_1, "Paracetamol", UNIT_PRICE_1, UNIT_COST_1,
                        2, 1L, START_INSTANT.plusSeconds(3600), EMPLOYEE_ID_1)
        );

        List<EmployeeDto> employees = List.of(
                buildEmployeeDto(EMPLOYEE_ID_1, EMPLOYEE_NAME_1, CUI_1)
        );

        given(saleRepository.findSalesWithMedicineBetweenDates(START_INSTANT, END_INSTANT)).willReturn(filteredSales);
        given(employeeClient.findAllEmployees()).willReturn(employees);

        // when
        List<ReportSalesPerEmployeeDto> result = saleService.getReportSalesMedicineEmployeeInRange(START_DATE, END_DATE);

        // then
        AssertionsForInterfaceTypes.assertThat(result).hasSize(1);
        verify(saleRepository).findSalesWithMedicineBetweenDates(START_INSTANT, END_INSTANT);
        verify(saleRepository, never()).findAllSalesWithMedicine();
    }

    @Test
    void shouldReturnEmptyReportWhenNoSalesInDateRange2() {
        // given
        given(saleRepository.findSalesWithMedicineBetweenDates(START_INSTANT, END_INSTANT)).willReturn(Collections.emptyList());

        // when
        List<ReportSalesPerEmployeeDto> result = saleService.getReportSalesMedicineEmployeeInRange(START_DATE, END_DATE);

        // then
        AssertionsForInterfaceTypes.assertThat(result).isEmpty();
    }

    @Test
    void shouldConvertDatesToInstantsCorrectly2() {
        // given
        LocalDate testStartDate = LocalDate.of(2023, 6, 1);
        LocalDate testEndDate = LocalDate.of(2023, 6, 30);
        Instant expectedStartInstant = testStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant expectedEndInstant = testEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        given(saleRepository.findSalesWithMedicineBetweenDates(expectedStartInstant, expectedEndInstant))
                .willReturn(Collections.emptyList());

        // when
        saleService.getReportSalesMedicineEmployeeInRange(testStartDate, testEndDate);

        // then
        verify(saleRepository).findSalesWithMedicineBetweenDates(expectedStartInstant, expectedEndInstant);
    }

    @Test
    void shouldReuseExistingEmployeeReportGenerationLogic() {
        // given
        List<SaleMedicineDto> testSales = List.of(
                buildSaleMedicineDtoWithEmployee(MEDICINE_ID_1, "Paracetamol", UNIT_PRICE_1, UNIT_COST_1,
                        2, 1L, SOLD_AT, EMPLOYEE_ID_1)
        );

        List<EmployeeDto> employees = List.of(
                buildEmployeeDto(EMPLOYEE_ID_1, EMPLOYEE_NAME_1, CUI_1)
        );

        given(saleRepository.findAllSalesWithMedicine()).willReturn(testSales);
        given(employeeClient.findAllEmployees()).willReturn(employees);

        // when
        List<ReportSalesPerEmployeeDto> result = saleService.getReportSalesMedicineEmployeeInRange(null, null);

        // then
        assertThat(result.get(0).employeeId()).isEqualTo(EMPLOYEE_ID_1);
        assertThat(result.get(0).totalSold()).isEqualTo(2);
    }

    @Test
    void shouldHandleFeignExceptionFromEmployeeClient() {
        // given
        List<SaleMedicineDto> testSales = List.of(
                buildSaleMedicineDtoWithEmployee(MEDICINE_ID_1, "Paracetamol", UNIT_PRICE_1, UNIT_COST_1,
                        2, 1L, SOLD_AT, EMPLOYEE_ID_1)
        );

        given(saleRepository.findAllSalesWithMedicine()).willReturn(testSales);
        given(employeeClient.findAllEmployees()).willThrow(FeignException.class);

        // when / then
        assertThatThrownBy(() -> saleService.getReportSalesMedicineEmployeeInRange(null, null))
                .isInstanceOf(RequestConflictException.class)
                .hasMessage("No se obtener empleados para el reporte");
    }

    /**
     * tests function getReportSalesTotal
     */
    @Test
    void shouldReturnEmptyReportWhenListIsNull() {
        // when
        ReportSalesTotal result = saleService.getReportSalesTotal(null);

        // then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.totalIncome());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void shouldReturnEmptyReportWhenListIsEmpty() {
        // when
        ReportSalesTotal result = saleService.getReportSalesTotal(Collections.emptyList());

        // then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.totalIncome());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void shouldCalculateTotalIncomeCorrectly() {
        // given
        SaleMedicineDto sale1 = SaleMedicineDto.builder()
                .medicineId(1L)
                .name("Paracetamol")
                .unitCost(BigDecimal.valueOf(1.0))
                .saleId(10L)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(5.0))
                .soldAt(Instant.now())
                .employeeId(100L)
                .build();

        SaleMedicineDto sale2 = SaleMedicineDto.builder()
                .medicineId(2L)
                .name("Ibuprofeno")
                .unitCost(BigDecimal.valueOf(2.0))
                .saleId(11L)
                .quantity(3)
                .unitPrice(BigDecimal.valueOf(4.0))
                .soldAt(Instant.now())
                .employeeId(101L)
                .build();

        List<SaleMedicineDto> sales = List.of(sale1, sale2);

        // when
        ReportSalesTotal result = saleService.getReportSalesTotal(sales);

        // then
        BigDecimal expectedIncome = BigDecimal.valueOf(2 * 5.0 + 3 * 4.0); // 10 + 12 = 22
        assertNotNull(result);
        assertEquals(expectedIncome, result.totalIncome());
        assertEquals(sales, result.items());
    }

    /**
     * tests function getReportSalesTotalInRange
     */
    @Test
    void shouldReturnTotalReportWhenDatesAreNull() {
        // given
        given(saleRepository.findAllSalesWithMedicine()).willReturn(SALE_MEDICINE_LIST);

        // when
        ReportSalesTotal result = saleService.getReportSalesTotalInRange(VALID_START_DATE, null);

        // then
        assertNotNull(result);
        assertEquals(SALE_MEDICINE_LIST, result.items());
    }


    @Test
    void shouldReturnTotalReportWhenDatesAreProvided() {
        // given
        ZoneId zoneId = ZoneId.systemDefault();
        Instant expectedStartInstant = VALID_START_DATE.atStartOfDay(zoneId).toInstant();
        Instant expectedEndInstant = VALID_END_DATE.atStartOfDay(zoneId).toInstant();

        given(saleRepository.findSalesWithMedicineBetweenDates(expectedStartInstant, expectedEndInstant))
                .willReturn(SALE_MEDICINE_LIST);

        // when
        ReportSalesTotal result = saleService.getReportSalesTotalInRange(VALID_START_DATE, VALID_END_DATE);

        // then
        assertNotNull(result);
        assertEquals(SALE_MEDICINE_LIST, result.items());
    }


    // Utilidad para crear objetos DTO
    private static SaleMedicineDto buildSampleSaleMedicineDto() {
        return SaleMedicineDto.builder()
                .medicineId(1L)
                .name("Paracetamol")
                .unitCost(BigDecimal.valueOf(1.5))
                .saleId(10L)
                .quantity(3)
                .unitPrice(BigDecimal.valueOf(2.0))
                .soldAt(Instant.now())
                .employeeId(100L)
                .build();
    }

    private EmployeeDto buildEmployeeDto(Long id, String fullName, String cui) {
        return EmployeeDto.builder()
                .id(id)
                .fullName(fullName)
                .cui(cui)
                .phone("12345678")
                .email("test@example.com")
                .isSpecialist(true)
                .areaName("Pharmacy")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private SaleMedicineDto buildSaleMedicineDtoWithEmployee(
            Long medicineId, String name,
            BigDecimal unitPrice, BigDecimal unitCost,
            Integer quantity, Long saleId, Instant soldAt, Long employeeId) {
        return SaleMedicineDto.builder()
                .medicineId(medicineId)
                .name(name)
                .unitPrice(unitPrice)
                .unitCost(unitCost)
                .quantity(quantity)
                .saleId(saleId)
                .soldAt(soldAt)
                .employeeId(employeeId)
                .build();
    }

    private SaleMedicineDto buildSaleMedicineDto(
            Long medicineId, String name,
            BigDecimal unitPrice, BigDecimal unitCost,
            Integer quantity, Long saleId, Instant soldAt) {
        return SaleMedicineDto.builder()
                .medicineId(medicineId)
                .name(name)
                .unitPrice(unitPrice)
                .unitCost(unitCost)
                .quantity(quantity)
                .saleId(saleId)
                .soldAt(soldAt)
                .build();
    }

    private MedicineEntity buildMedicineEntity(Long id, String name, int stock) {
        return MedicineEntity.builder()
                .id(id)
                .name(name)
                .unitPrice(UNIT_PRICE)
                .unitCost(UNIT_COST)
                .stock(stock)
                .minStock(10)
                .build();
    }

    private MedicineEntity buildMedicineEntity(Long id) {
        return MedicineEntity.builder()
                .id(id)
                .name(MEDICINE_NAME)
                .unitPrice(UNIT_PRICE)
                .unitCost(UNIT_COST)
                .stock(100)
                .minStock(10)
                .build();
    }

    private ItemSaleDto buildItemSaleDto(Long medicineId, Integer quantity) {
        return ItemSaleDto.builder()
                .medicineId(medicineId)
                .quantity(quantity)
                .build();
    }

    private ItemSaleDto buildValidItemSaleDto() {
        return ItemSaleDto.builder()
                .quantity(VALID_QUANTITY)
                .medicineId(VALID_MEDICINE_ID)
                .build();
    }

    private EmployeeDto buildValidEmployeeDto(String cui) {
        return EmployeeDto.builder()
                .id(1L)
                .fullName("John Doe")
                .cui(cui)
                .phone("12345678")
                .email("john.doe@example.com")
                .isSpecialist(true)
                .areaName("Pharmacy")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

}