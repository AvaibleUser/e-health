package org.ehealth.hr.service;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.ehealth.hr.domain.dto.AreaResponseDto;
import org.ehealth.hr.domain.dto.CreateAreaDto;
import org.ehealth.hr.domain.dto.UpdateAreaDto;
import org.ehealth.hr.domain.entity.AreaEntity;
import org.ehealth.hr.domain.exception.BusinessException;
import org.ehealth.hr.domain.exception.ResourceNotFoundException;
import org.ehealth.hr.repository.AreaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

    private static final String VALID_AREA_NAME = "Development";
    private static final String DUPLICATE_AREA_NAME = "Existing Area";
    private static final String AREA_NAME_WITH_WHITESPACES = "  Marketing  ";

    private static final Long AREA_ID_1 = 1L;
    private static final Long AREA_ID_2 = 2L;
    private static final String AREA_NAME_1 = "Development";
    private static final String AREA_NAME_2 = "Marketing";
    private static final Instant CREATED_AT = Instant.parse("2023-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2023-01-02T00:00:00Z");

    private static final Long VALID_AREA_ID = 1L;
    private static final Long INVALID_AREA_ID = 99L;
    private static final String CURRENT_AREA_NAME = "Development";
    private static final String NEW_VALID_AREA_NAME = "Engineering";
    private static final String NEW_DUPLICATE_AREA_NAME = "Marketing";
    private static final String NEW_NAME_WITH_WHITESPACES = "  QA  ";

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private AreaService areaService;

    @BeforeEach
    void setUp() {

    }

    /**
     * test function create
     */
    @Test
    void shouldCreateAreaWhenNameIsValidAndNotExists() {
        // given
        CreateAreaDto validCreateAreaDto = buildCreateAreaDto(VALID_AREA_NAME);
        AreaEntity savedAreaEntity = buildAreaEntity(1L, VALID_AREA_NAME);
        AreaResponseDto expectedResponse = buildAreaResponseDto(savedAreaEntity);

        given(areaRepository.existsByNameIgnoreCase(VALID_AREA_NAME)).willReturn(false);
        given(areaRepository.save(any(AreaEntity.class))).willReturn(savedAreaEntity);

        // when
        AreaResponseDto result = areaService.create(validCreateAreaDto);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(areaRepository).existsByNameIgnoreCase(VALID_AREA_NAME);
        verify(areaRepository).save(argThat(area ->
                area.getName().equals(VALID_AREA_NAME) && area.getId() == null
        ));
    }

    @Test
    void shouldTrimAreaNameWhenCreatingArea() {
        // given
        CreateAreaDto createAreaDto = buildCreateAreaDto(AREA_NAME_WITH_WHITESPACES);
        AreaEntity savedAreaEntity = buildAreaEntity(1L, AREA_NAME_WITH_WHITESPACES.trim());
        AreaResponseDto expectedResponse = buildAreaResponseDto(savedAreaEntity);

        given(areaRepository.existsByNameIgnoreCase(AREA_NAME_WITH_WHITESPACES)).willReturn(false);
        given(areaRepository.save(any(AreaEntity.class))).willReturn(savedAreaEntity);

        // when
        AreaResponseDto result = areaService.create(createAreaDto);

        // then
        assertThat(result.name()).isEqualTo(AREA_NAME_WITH_WHITESPACES.trim());
        verify(areaRepository).save(argThat(area ->
                area.getName().equals(AREA_NAME_WITH_WHITESPACES.trim())
        ));
    }

    @Test
    void shouldThrowBusinessExceptionWhenAreaNameAlreadyExists() {
        // given
        CreateAreaDto duplicateCreateAreaDto = buildCreateAreaDto(DUPLICATE_AREA_NAME);

        given(areaRepository.existsByNameIgnoreCase(DUPLICATE_AREA_NAME)).willReturn(true);

        // when - then
        assertThatThrownBy(() -> areaService.create(duplicateCreateAreaDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El area ya existe");

        verify(areaRepository).existsByNameIgnoreCase(DUPLICATE_AREA_NAME);
        verify(areaRepository, never()).save(any());
    }

    /**
     * tests function findAll
     */
    @Test
    void shouldReturnEmptyListWhenNoAreasExist() {
        // given
        given(areaRepository.findAll()).willReturn(Collections.emptyList());

        // when
        List<AreaResponseDto> result = areaService.findAll();

        // then
        AssertionsForInterfaceTypes.assertThat(result).isEmpty();
        verify(areaRepository).findAll();
    }

    @Test
    void shouldReturnListWithOneAreaWhenOnlyOneExists() {
        // given
        AreaEntity singleArea = buildAreaEntity(AREA_ID_1, AREA_NAME_1, CREATED_AT, UPDATED_AT);
        AreaResponseDto expectedResponse = buildAreaResponseDto(singleArea);

        given(areaRepository.findAll()).willReturn(List.of(singleArea));

        // when
        List<AreaResponseDto> result = areaService.findAll();

        // then
        AssertionsForInterfaceTypes.assertThat(result)
                .hasSize(1)
                .first()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(areaRepository).findAll();
    }

    @Test
    void shouldReturnAllAreasOrderedByIdWhenMultipleExist() {
        // given
        AreaEntity area1 = buildAreaEntity(AREA_ID_1, AREA_NAME_1, CREATED_AT, UPDATED_AT);
        AreaEntity area2 = buildAreaEntity(AREA_ID_2, AREA_NAME_2, CREATED_AT.plusSeconds(3600), UPDATED_AT.plusSeconds(3600));

        List<AreaEntity> areas = List.of(area1, area2);
        List<AreaResponseDto> expectedResponses = areas.stream()
                .map(this::buildAreaResponseDto)
                .toList();

        given(areaRepository.findAll()).willReturn(areas);

        // when
        List<AreaResponseDto> result = areaService.findAll();

        // then
        AssertionsForInterfaceTypes.assertThat(result)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(expectedResponses);

        verify(areaRepository).findAll();
    }

    /**
     * tests function updateName
     */
    @Test
    void shouldUpdateAreaNameWhenIdExistsAndNewNameIsUnique() {
        // given
        AreaEntity existingArea = buildAreaEntity(VALID_AREA_ID, CURRENT_AREA_NAME);
        UpdateAreaDto updateDto = buildUpdateAreaDto(NEW_VALID_AREA_NAME);
        AreaEntity updatedArea = buildAreaEntity(VALID_AREA_ID, NEW_VALID_AREA_NAME);
        AreaResponseDto expectedResponse = buildAreaResponseDto(updatedArea);

        given(areaRepository.findById(VALID_AREA_ID)).willReturn(Optional.of(existingArea));
        given(areaRepository.existsByNameIgnoreCase(NEW_VALID_AREA_NAME)).willReturn(false);
        given(areaRepository.save(any(AreaEntity.class))).willReturn(updatedArea);

        // when
        AreaResponseDto result = areaService.updateName(VALID_AREA_ID, updateDto);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(areaRepository).findById(VALID_AREA_ID);
        verify(areaRepository).existsByNameIgnoreCase(NEW_VALID_AREA_NAME);
        verify(areaRepository).save(argThat(area ->
                area.getId().equals(VALID_AREA_ID) &&
                        area.getName().equals(NEW_VALID_AREA_NAME)
        ));
    }

    @Test
    void shouldTrimNewNameWhenUpdatingAreaName() {
        // given
        AreaEntity existingArea = buildAreaEntity(VALID_AREA_ID, CURRENT_AREA_NAME);
        UpdateAreaDto updateDto = buildUpdateAreaDto(NEW_NAME_WITH_WHITESPACES);
        AreaEntity updatedArea = buildAreaEntity(VALID_AREA_ID, NEW_NAME_WITH_WHITESPACES.trim());

        given(areaRepository.findById(VALID_AREA_ID)).willReturn(Optional.of(existingArea));

        given(areaRepository.existsByNameIgnoreCase(NEW_NAME_WITH_WHITESPACES)).willReturn(false);

        given(areaRepository.save(any(AreaEntity.class))).willReturn(updatedArea);

        // when
        AreaResponseDto result = areaService.updateName(VALID_AREA_ID, updateDto);

        // then
        assertThat(result.name()).isEqualTo(NEW_NAME_WITH_WHITESPACES.trim());
        verify(areaRepository).save(argThat(area ->
                area.getName().equals(NEW_NAME_WITH_WHITESPACES.trim())
        ));
    }


    @Test
    void shouldThrowResourceNotFoundExceptionWhenAreaIdDoesNotExist() {
        // given
        UpdateAreaDto updateDto = buildUpdateAreaDto(NEW_VALID_AREA_NAME);

        given(areaRepository.findById(INVALID_AREA_ID)).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> areaService.updateName(INVALID_AREA_ID, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Area no encontrada con el id " + INVALID_AREA_ID);

        verify(areaRepository).findById(INVALID_AREA_ID);
        verify(areaRepository, never()).existsByNameIgnoreCase(any());
        verify(areaRepository, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenNewNameAlreadyExists() {
        // given
        AreaEntity existingArea = buildAreaEntity(VALID_AREA_ID, CURRENT_AREA_NAME);
        UpdateAreaDto updateDto = buildUpdateAreaDto(NEW_DUPLICATE_AREA_NAME);

        given(areaRepository.findById(VALID_AREA_ID)).willReturn(Optional.of(existingArea));
        given(areaRepository.existsByNameIgnoreCase(NEW_DUPLICATE_AREA_NAME)).willReturn(true);

        // when - then
        assertThatThrownBy(() -> areaService.updateName(VALID_AREA_ID, updateDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El area con ese nombre ya existe");

        verify(areaRepository).findById(VALID_AREA_ID);
        verify(areaRepository).existsByNameIgnoreCase(NEW_DUPLICATE_AREA_NAME);
        verify(areaRepository, never()).save(any());
    }


    // Utility methods using builders
    private CreateAreaDto buildCreateAreaDto(String name) {
        return new CreateAreaDto(name);
    }

    private AreaEntity buildAreaEntity(Long id, String name, Instant createdAt, Instant updatedAt) {
        return AreaEntity.builder()
                .id(id)
                .name(name)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private AreaEntity buildAreaEntity(Long id, String name) {
        return AreaEntity.builder()
                .id(id)
                .name(name)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private AreaResponseDto buildAreaResponseDto(AreaEntity entity) {
        return AreaResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private UpdateAreaDto buildUpdateAreaDto(String newName) {
        return new UpdateAreaDto(newName);
    }




}