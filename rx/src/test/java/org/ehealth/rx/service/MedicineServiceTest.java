package org.ehealth.rx.service;

import org.ehealth.rx.domain.dto.CreateMedicineDto;
import org.ehealth.rx.domain.dto.MedicineDto;
import org.ehealth.rx.domain.entity.MedicineEntity;
import org.ehealth.rx.domain.exception.BadRequestException;
import org.ehealth.rx.domain.exception.RequestConflictException;
import org.ehealth.rx.repository.MedicineRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import feign.FeignException;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineService medicineService;


    private static final String VALID_MEDICINE_NAME = "Ibuprofeno";
    private static final String DUPLICATED_MEDICINE_NAME = "Paracetamol";
    private static final BigDecimal VALID_UNIT_PRICE = new BigDecimal("19.99");
    private static final BigDecimal VALID_UNIT_COST = new BigDecimal("15.50");
    private static final int VALID_STOCK = 100;
    private static final int VALID_MIN_STOCK = 10;
    private static final Long VALID_MEDICINE_ID = 1L;
    private static final Long INVALID_MEDICINE_ID = 99L;
    private static final String NOT_FOUND_MESSAGE = "La medicina no existe";


    @BeforeEach
    void setUp() {

    }

    /**
     * tests function create
     */
    @Test
    void shouldCreateMedicineWhenNameIsUnique() {
        // given
        CreateMedicineDto validMedicineDto = buildValidMedicineDto(VALID_MEDICINE_NAME);
        given(medicineRepository.existsByNameIgnoreCase(VALID_MEDICINE_NAME)).willReturn(false);
        given(medicineRepository.save(any(MedicineEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        medicineService.create(validMedicineDto);

        // then
        then(medicineRepository).should().existsByNameIgnoreCase(VALID_MEDICINE_NAME);
        then(medicineRepository).should().save(any(MedicineEntity.class));
    }

    @Test
    void shouldThrowConflictExceptionWhenMedicineNameAlreadyExists() {
        // given
        CreateMedicineDto duplicatedMedicineDto = buildValidMedicineDto(DUPLICATED_MEDICINE_NAME);
        given(medicineRepository.existsByNameIgnoreCase(DUPLICATED_MEDICINE_NAME)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> medicineService.create(duplicatedMedicineDto))
                .isInstanceOf(RequestConflictException.class)
                .hasMessageContaining(DUPLICATED_MEDICINE_NAME);

        then(medicineRepository).should().existsByNameIgnoreCase(DUPLICATED_MEDICINE_NAME);
        then(medicineRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void shouldMapAllFieldsCorrectlyWhenCreatingMedicine() {
        // given
        CreateMedicineDto medicineDto = buildValidMedicineDto(VALID_MEDICINE_NAME);
        given(medicineRepository.existsByNameIgnoreCase(VALID_MEDICINE_NAME)).willReturn(false);

        ArgumentCaptor<MedicineEntity> medicineCaptor = ArgumentCaptor.forClass(MedicineEntity.class);
        given(medicineRepository.save(medicineCaptor.capture())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        medicineService.create(medicineDto);

        // then
        MedicineEntity savedMedicine = medicineCaptor.getValue();
        assertThat(savedMedicine.getName()).isEqualTo(VALID_MEDICINE_NAME);
        assertThat(savedMedicine.getUnitPrice()).isEqualTo(VALID_UNIT_PRICE);
        assertThat(savedMedicine.getUnitCost()).isEqualTo(VALID_UNIT_COST);
        assertThat(savedMedicine.getStock()).isEqualTo(VALID_STOCK);
        assertThat(savedMedicine.getMinStock()).isEqualTo(VALID_MIN_STOCK);
    }

    /**
     * tests function findAll
     */
    @Test
    void shouldReturnEmptyListWhenNoMedicinesExist() {
        // given
        given(medicineRepository.findAllByOrderByCreatedAtDesc(MedicineDto.class))
                .willReturn(Collections.emptyList());

        // when
        List<MedicineDto> result = medicineService.findAll();

        // then
        AssertionsForInterfaceTypes.assertThat(result).isEmpty();
        then(medicineRepository).should().findAllByOrderByCreatedAtDesc(MedicineDto.class);
    }

    @Test
    void shouldReturnAllMedicinesOrderedByCreationDateDesc() {
        // given
        MedicineDto olderMedicine = MedicineDto.builder()
                .id(1L)
                .name("Medicine A")
                .unitPrice(new BigDecimal("10.00"))
                .unitCost(new BigDecimal("8.00"))
                .stock(50)
                .minStock(5)
                .createdAt(Instant.now().minus(2, ChronoUnit.DAYS))
                .build();

        MedicineDto newerMedicine = MedicineDto.builder()
                .id(2L)
                .name(VALID_MEDICINE_NAME)
                .unitPrice(VALID_UNIT_PRICE)
                .unitCost(VALID_UNIT_COST)
                .stock(VALID_STOCK)
                .minStock(VALID_MIN_STOCK)
                .createdAt(Instant.now())
                .build();

        List<MedicineDto> expectedMedicines = List.of(newerMedicine, olderMedicine);
        given(medicineRepository.findAllByOrderByCreatedAtDesc(MedicineDto.class))
                .willReturn(expectedMedicines);

        // when
        List<MedicineDto> result = medicineService.findAll();

        // then
        AssertionsForInterfaceTypes.assertThat(result)
                .hasSize(2)
                .containsExactlyElementsOf(expectedMedicines);

        then(medicineRepository).should().findAllByOrderByCreatedAtDesc(MedicineDto.class);
    }

    @Test
    void shouldReturnAllFieldsCorrectlyMapped() {
        // given
        MedicineDto expectedMedicine = MedicineDto.builder()
                .id(1L)
                .name(VALID_MEDICINE_NAME)
                .unitPrice(VALID_UNIT_PRICE)
                .unitCost(VALID_UNIT_COST)
                .stock(VALID_STOCK)
                .minStock(VALID_MIN_STOCK)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        given(medicineRepository.findAllByOrderByCreatedAtDesc(MedicineDto.class))
                .willReturn(List.of(expectedMedicine));

        // when
        List<MedicineDto> result = medicineService.findAll();

        // then
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .isEqualTo(expectedMedicine);
    }

    /**
     * tests function findById
     */
    @Test
    void shouldReturnMedicineDtoWhenIdExists() {
        // given
        MedicineEntity existingMedicine = buildValidMedicineEntity(VALID_MEDICINE_ID);
        given(medicineRepository.findById(VALID_MEDICINE_ID))
                .willReturn(Optional.of(existingMedicine));

        // when
        MedicineDto result = medicineService.findById(VALID_MEDICINE_ID);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(mapEntityToDto(existingMedicine));

        then(medicineRepository).should().findById(VALID_MEDICINE_ID);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenIdDoesNotExist() {
        // given
        given(medicineRepository.findById(INVALID_MEDICINE_ID))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> medicineService.findById(INVALID_MEDICINE_ID))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(NOT_FOUND_MESSAGE);

        then(medicineRepository).should().findById(INVALID_MEDICINE_ID);
    }

    @Test
    void shouldVerifyCompleteFieldMappingWhenEntityIsConvertedToDto() {
        // given
        MedicineEntity medicineWithAllFields = MedicineEntity.builder()
                .id(VALID_MEDICINE_ID)
                .name(VALID_MEDICINE_NAME)
                .unitPrice(VALID_UNIT_PRICE)
                .unitCost(VALID_UNIT_COST)
                .stock(VALID_STOCK)
                .minStock(VALID_MIN_STOCK)
                .createdAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .updatedAt(Instant.now())
                .build();

        given(medicineRepository.findById(VALID_MEDICINE_ID))
                .willReturn(Optional.of(medicineWithAllFields));

        // when
        MedicineDto result = medicineService.findById(VALID_MEDICINE_ID);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(mapEntityToDto(medicineWithAllFields));
    }

    /**
     * tests function update
     */
    @Test
    void shouldUpdateAllFieldsWhenMedicineExists() {
        // given
        MedicineEntity existingMedicine = buildValidMedicineEntity(VALID_MEDICINE_ID);
        CreateMedicineDto updateDto = buildValidMedicineDto("Nuevo nombre");

        given(medicineRepository.findById(VALID_MEDICINE_ID))
                .willReturn(Optional.of(existingMedicine));
        given(medicineRepository.save(any(MedicineEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        medicineService.update(VALID_MEDICINE_ID, updateDto);

        // then
        assertThat(existingMedicine)
                .extracting(
                        MedicineEntity::getName,
                        MedicineEntity::getUnitPrice,
                        MedicineEntity::getUnitCost,
                        MedicineEntity::getStock,
                        MedicineEntity::getMinStock
                )
                .containsExactly(
                        updateDto.name(),
                        updateDto.unitPrice(),
                        updateDto.unitCost(),
                        updateDto.stock(),
                        updateDto.minStock()
                );

        then(medicineRepository).should().findById(VALID_MEDICINE_ID);
        then(medicineRepository).should().save(existingMedicine);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenMedicineNotFound() {
        // given
        CreateMedicineDto updateDto = buildValidMedicineDto(VALID_MEDICINE_NAME);
        given(medicineRepository.findById(INVALID_MEDICINE_ID))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> medicineService.update(INVALID_MEDICINE_ID, updateDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(NOT_FOUND_MESSAGE);

        then(medicineRepository).should().findById(INVALID_MEDICINE_ID);
        then(medicineRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void shouldVerifyTransactionalAnnotationIsPresent() throws NoSuchMethodException {
        // given
        Method updateMethod = MedicineService.class.getMethod("update", Long.class, CreateMedicineDto.class);

        // when
        Transactional transactionalAnnotation = updateMethod.getAnnotation(Transactional.class);

        // then
        assertThat(transactionalAnnotation).isNotNull();
    }

    private MedicineEntity buildValidMedicineEntity(Long id) {
        return MedicineEntity.builder()
                .id(id)
                .name(VALID_MEDICINE_NAME)
                .unitPrice(VALID_UNIT_PRICE)
                .unitCost(VALID_UNIT_COST)
                .stock(VALID_STOCK)
                .minStock(VALID_MIN_STOCK)
                .createdAt(Instant.now())
                .build();
    }

    private MedicineDto mapEntityToDto(MedicineEntity entity) {
        return MedicineDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .unitPrice(entity.getUnitPrice())
                .unitCost(entity.getUnitCost())
                .stock(entity.getStock())
                .minStock(entity.getMinStock())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private CreateMedicineDto buildValidMedicineDto(String name) {
        return CreateMedicineDto.builder()
                .name(name)
                .unitPrice(VALID_UNIT_PRICE)
                .unitCost(VALID_UNIT_COST)
                .stock(VALID_STOCK)
                .minStock(VALID_MIN_STOCK)
                .build();
    }


}