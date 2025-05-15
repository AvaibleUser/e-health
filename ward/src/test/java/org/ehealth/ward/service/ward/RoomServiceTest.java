package org.ehealth.ward.service.ward;

import org.ehealth.ward.domain.dto.ward.room.CreateRoom;
import org.ehealth.ward.domain.dto.ward.room.RoomResponseDto;
import org.ehealth.ward.domain.entity.ward.RoomEntity;
import org.ehealth.ward.domain.exception.BusinessException;
import org.ehealth.ward.domain.exception.ResourceNotFoundException;
import org.ehealth.ward.repository.ward.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    // Constants
    private static final String VALID_ROOM_NUMBER = "101A";
    private static final String EXISTING_ROOM_NUMBER = "202B";
    private static final String ROOM_NUMBER_WITH_WHITESPACE = " 303C ";
    private static final BigDecimal VALID_COST_PER_DAY = new BigDecimal("150.50");
    private static final BigDecimal ZERO_COST_PER_DAY = BigDecimal.ZERO;

    private static final Instant NOW = Instant.now();
    private static final BigDecimal STANDARD_ROOM_COST = new BigDecimal("150.00");
    private static final BigDecimal PREMIUM_ROOM_COST = new BigDecimal("300.00");

    private static final Long EXISTING_ROOM_ID = 1L;
    private static final Long NON_EXISTING_ROOM_ID = 99L;
    private static final String OLD_ROOM_NUMBER = "101";
    private static final String NEW_ROOM_NUMBER = "102";
    private static final String DUPLICATE_ROOM_NUMBER = "201";
    private static final BigDecimal UPDATED_ROOM_COST = new BigDecimal("200.00");

    @BeforeEach
    void setUp() {

    }

    /**
     * tests function create
     */

    @Test
    void shouldCreateRoomWhenNumberIsUnique() {
        // given
        CreateRoom validRequest = buildCreateRoomDto(VALID_ROOM_NUMBER, VALID_COST_PER_DAY);
        RoomEntity savedEntity = buildRoomEntity(VALID_ROOM_NUMBER.trim(), VALID_COST_PER_DAY);

        given(roomRepository.existsByNumberIgnoreCase(VALID_ROOM_NUMBER)).willReturn(false);
        given(roomRepository.save(any(RoomEntity.class))).willReturn(savedEntity);

        // when
        RoomResponseDto result = roomService.create(validRequest);

        // then
        assertNotNull(result);
        assertEquals(VALID_ROOM_NUMBER.trim(), result.number());
        assertEquals(VALID_COST_PER_DAY, result.costPerDay());
        assertFalse(result.isOccupied());
        assertFalse(result.underMaintenance());

        verify(roomRepository).existsByNumberIgnoreCase(VALID_ROOM_NUMBER);
        verify(roomRepository).save(any(RoomEntity.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenRoomNumberExists() {
        // given
        CreateRoom duplicateRequest = buildCreateRoomDto(EXISTING_ROOM_NUMBER, VALID_COST_PER_DAY);

        given(roomRepository.existsByNumberIgnoreCase(EXISTING_ROOM_NUMBER)).willReturn(true);

        // when / then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> roomService.create(duplicateRequest));

        assertEquals("El numero de habitacion ya existe", exception.getMessage());
        verify(roomRepository).existsByNumberIgnoreCase(EXISTING_ROOM_NUMBER);
        verify(roomRepository, never()).save(any());
    }

    @Test
    void shouldSetDefaultStatusFlagsWhenCreatingRoom() {
        // given
        CreateRoom validRequest = buildCreateRoomDto(VALID_ROOM_NUMBER, VALID_COST_PER_DAY);
        RoomEntity savedEntity = buildRoomEntity(VALID_ROOM_NUMBER, VALID_COST_PER_DAY);

        given(roomRepository.existsByNumberIgnoreCase(VALID_ROOM_NUMBER)).willReturn(false);
        given(roomRepository.save(any(RoomEntity.class))).willReturn(savedEntity);

        // when
        RoomResponseDto result = roomService.create(validRequest);

        // then
        assertFalse(result.isOccupied());
        assertFalse(result.underMaintenance());
    }

    /**
     * tests function findAll
     */
    @Test
    void shouldReturnEmptyListWhenNoRoomsExist() {
        // given
        given(roomRepository.findAll()).willReturn(Collections.emptyList());

        // when
        List<RoomResponseDto> result = roomService.findAll();

        // then
        assertThat(result).isEmpty();
        verify(roomRepository).findAll();
    }

    @Test
    void shouldReturnListOfRoomsWhenRoomsExist() {
        // given
        List<RoomEntity> rooms = List.of(
                buildRoomEntity(1L, "101", STANDARD_ROOM_COST, false, false),
                buildRoomEntity(2L, "201", PREMIUM_ROOM_COST, true, false)
        );
        given(roomRepository.findAll()).willReturn(rooms);

        // when
        List<RoomResponseDto> result = roomService.findAll();

        // then
        assertThat(result)
                .hasSize(2)
                .extracting(RoomResponseDto::number)
                .containsExactly("101", "201");

        verify(roomRepository).findAll();
    }

    @Test
    void shouldMapAllRoomPropertiesCorrectly() {
        // given
        RoomEntity roomEntity = buildRoomEntity(
                1L,
                "101",
                STANDARD_ROOM_COST,
                true,
                false,
                NOW.minus(1, ChronoUnit.DAYS),
                NOW
        );
        given(roomRepository.findAll()).willReturn(List.of(roomEntity));

        // when
        RoomResponseDto result = roomService.findAll().get(0);

        // then
        assertThat(result)
                .extracting(
                        RoomResponseDto::id,
                        RoomResponseDto::number,
                        RoomResponseDto::costPerDay,
                        RoomResponseDto::isOccupied,
                        RoomResponseDto::underMaintenance,
                        RoomResponseDto::createdAt,
                        RoomResponseDto::updatedAt
                )
                .containsExactly(
                        1L,
                        "101",
                        STANDARD_ROOM_COST,
                        true,
                        false,
                        NOW.minus(1, ChronoUnit.DAYS),
                        NOW
                );
    }


    /**
     * tests function updateName
     */

    @Test
    void shouldUpdateRoomNameAndCostWhenDataIsValid() {
        // given
        RoomEntity existingRoom = buildRoomEntity(EXISTING_ROOM_ID, OLD_ROOM_NUMBER, STANDARD_ROOM_COST);
        CreateRoom updateDto = buildCreateRoomDto(NEW_ROOM_NUMBER, UPDATED_ROOM_COST);

        given(roomRepository.findById(EXISTING_ROOM_ID)).willReturn(Optional.of(existingRoom));
        given(roomRepository.existsByNumberIgnoreCase(NEW_ROOM_NUMBER)).willReturn(false);
        given(roomRepository.save(any(RoomEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        RoomResponseDto result = roomService.updateName(EXISTING_ROOM_ID, updateDto);

        // then
        assertThat(result)
                .extracting(RoomResponseDto::number, RoomResponseDto::costPerDay)
                .containsExactly(NEW_ROOM_NUMBER.trim(), UPDATED_ROOM_COST);

        verify(roomRepository).findById(EXISTING_ROOM_ID);
        verify(roomRepository).existsByNumberIgnoreCase(NEW_ROOM_NUMBER);
        verify(roomRepository).save(existingRoom);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenRoomNotFound() {
        // given
        CreateRoom updateDto = buildCreateRoomDto(NEW_ROOM_NUMBER, UPDATED_ROOM_COST);
        given(roomRepository.findById(NON_EXISTING_ROOM_ID)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> roomService.updateName(NON_EXISTING_ROOM_ID, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Habitacion no encontrada");

        verify(roomRepository).findById(NON_EXISTING_ROOM_ID);
        verify(roomRepository, never()).existsByNumberIgnoreCase(any());
        verify(roomRepository, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenRoomNumberExistsUpdate() {
        // given
        RoomEntity existingRoom = buildRoomEntity(EXISTING_ROOM_ID, OLD_ROOM_NUMBER, STANDARD_ROOM_COST);
        CreateRoom updateDto = buildCreateRoomDto(DUPLICATE_ROOM_NUMBER, UPDATED_ROOM_COST);

        given(roomRepository.findById(EXISTING_ROOM_ID)).willReturn(Optional.of(existingRoom));
        given(roomRepository.existsByNumberIgnoreCase(DUPLICATE_ROOM_NUMBER)).willReturn(true);

        // when / then
        assertThatThrownBy(() -> roomService.updateName(EXISTING_ROOM_ID, updateDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Numero de Habitacion ya existe");

        verify(roomRepository).findById(EXISTING_ROOM_ID);
        verify(roomRepository).existsByNumberIgnoreCase(DUPLICATE_ROOM_NUMBER);
        verify(roomRepository, never()).save(any());
    }

    @Test
    void shouldNotCheckForDuplicateWhenNumberNotChanged() {
        // given
        RoomEntity existingRoom = buildRoomEntity(EXISTING_ROOM_ID, OLD_ROOM_NUMBER, STANDARD_ROOM_COST);
        CreateRoom updateDto = buildCreateRoomDto(OLD_ROOM_NUMBER, UPDATED_ROOM_COST);

        given(roomRepository.findById(EXISTING_ROOM_ID)).willReturn(Optional.of(existingRoom));
        given(roomRepository.save(any(RoomEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        RoomResponseDto result = roomService.updateName(EXISTING_ROOM_ID, updateDto);

        // then
        assertThat(result.costPerDay()).isEqualTo(UPDATED_ROOM_COST);
        verify(roomRepository, never()).existsByNumberIgnoreCase(any());
    }


    //utils
    private CreateRoom buildCreateRoomDto(String number, BigDecimal cost) {
        return CreateRoom.builder()
                .number(number)
                .costPerDay(cost)
                .build();
    }

    private RoomEntity buildRoomEntity(String number, BigDecimal cost) {
        return RoomEntity.builder()
                .id(1L)
                .number(number)
                .costPerDay(cost)
                .isOccupied(false)
                .underMaintenance(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private RoomEntity buildRoomEntity(Long id, String number, BigDecimal costPerDay,
                                       boolean isOccupied, boolean underMaintenance) {
        return buildRoomEntity(id, number, costPerDay, isOccupied, underMaintenance, NOW, NOW);
    }

    private RoomEntity buildRoomEntity(Long id, String number, BigDecimal costPerDay,
                                       boolean isOccupied, boolean underMaintenance,
                                       Instant createdAt, Instant updatedAt) {
        return RoomEntity.builder()
                .id(id)
                .number(number)
                .costPerDay(costPerDay)
                .isOccupied(isOccupied)
                .underMaintenance(underMaintenance)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private RoomEntity buildRoomEntity(Long id, String number, BigDecimal costPerDay) {
        return RoomEntity.builder()
                .id(id)
                .number(number)
                .costPerDay(costPerDay)
                .isOccupied(false)
                .underMaintenance(false)
                .createdAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .updatedAt(Instant.now())
                .build();
    }


}