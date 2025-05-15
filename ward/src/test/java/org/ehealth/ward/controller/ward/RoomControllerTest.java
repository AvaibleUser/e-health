package org.ehealth.ward.controller.ward;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.ward.domain.dto.ward.room.CreateRoom;
import org.ehealth.ward.domain.dto.ward.room.RoomResponseDto;
import org.ehealth.ward.service.ward.RoomService;
import org.ehealth.ward.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.ehealth.ward.util.JwtBuilder.jwt;
import static org.ehealth.ward.util.ThenMockAlias.thenMock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(classes = RoomController.class, controllers = RoomController.class)
class RoomControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected RoomService roomService;

    // URL Constants
    private static final String BASE_URL = "/api/ward/v1/rooms";
    private static final String ALL_ROOMS_ENDPOINT = "/all";
    private static final String API_CONTEXT_PATH = "/api/ward";

    // Room Constants
    private static final Long ROOM_ID_1 = 1L;
    private static final Long ROOM_ID_2 = 2L;
    private static final String ROOM_NUMBER_A101 = "A101";
    private static final String ROOM_NUMBER_B201 = "B201";
    private static final String ROOM_NUMBER_C303 = "C303";
    private static final String ROOM_NUMBER_UPDATED = "Updated101";

    // Cost Constants
    private static final BigDecimal COST_100_50 = new BigDecimal("100.50");
    private static final BigDecimal COST_120_00 = new BigDecimal("120.00");
    private static final BigDecimal COST_130_00 = new BigDecimal("130.00");
    private static final BigDecimal COST_150_00 = new BigDecimal("150.00");

    // Status Constants
    private static final boolean OCCUPIED = true;
    private static final boolean NOT_OCCUPIED = false;
    private static final boolean UNDER_MAINTENANCE = true;
    private static final boolean NOT_UNDER_MAINTENANCE = false;

    // Date Constants
    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-01T00:00:00Z");

    // User Constants
    private static final Long ADMIN_USER_ID = 1L;
    private static final String ROLE_ADMIN = "ADMIN";

    @Test
    void createRoom_shouldReturnCreated() throws Exception {
        // given
        CreateRoom createRequest = new CreateRoom(ROOM_NUMBER_A101, COST_100_50);

        RoomResponseDto expectedResponse = RoomResponseDto.builder()
                .id(ROOM_ID_1)
                .number(ROOM_NUMBER_A101)
                .costPerDay(COST_100_50)
                .isOccupied(NOT_OCCUPIED)
                .underMaintenance(NOT_UNDER_MAINTENANCE)
                .createdAt(FIXED_INSTANT)
                .updatedAt(FIXED_INSTANT)
                .build();

        given(roomService.create(createRequest)).willReturn(expectedResponse);

        // when
        ResultActions result = mockMvc.perform(post(BASE_URL)
                .contextPath(API_CONTEXT_PATH)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createRequest))
                .with(jwt(ADMIN_USER_ID, ROLE_ADMIN)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse)));

        thenMock(roomService).should().create(createRequest);
    }

    @Test
    void findAllRooms_shouldReturnList() throws Exception {
        // given
        RoomResponseDto roomB201 = RoomResponseDto.builder()
                .id(ROOM_ID_1)
                .number(ROOM_NUMBER_B201)
                .costPerDay(COST_120_00)
                .isOccupied(NOT_OCCUPIED)
                .underMaintenance(NOT_UNDER_MAINTENANCE)
                .createdAt(FIXED_INSTANT)
                .updatedAt(FIXED_INSTANT)
                .build();

        RoomResponseDto roomC303 = RoomResponseDto.builder()
                .id(ROOM_ID_2)
                .number(ROOM_NUMBER_C303)
                .costPerDay(COST_150_00)
                .isOccupied(OCCUPIED)
                .underMaintenance(NOT_UNDER_MAINTENANCE)
                .createdAt(FIXED_INSTANT)
                .updatedAt(FIXED_INSTANT)
                .build();

        List<RoomResponseDto> expectedRooms = List.of(roomB201, roomC303);

        given(roomService.findAll()).willReturn(expectedRooms);

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL + ALL_ROOMS_ENDPOINT)
                .contextPath(API_CONTEXT_PATH)
                .with(jwt(ADMIN_USER_ID, ROLE_ADMIN))); // <-- paréntesis cerrado correctamente aquí

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedRooms)));

        thenMock(roomService).should().findAll();
    }


    @Test
    void updateRoomName_shouldReturnUpdatedRoom() throws Exception {
        // given
        CreateRoom updateRequest = new CreateRoom(ROOM_NUMBER_UPDATED, COST_130_00);

        RoomResponseDto expectedResponse = RoomResponseDto.builder()
                .id(ROOM_ID_1)
                .number(ROOM_NUMBER_UPDATED)
                .costPerDay(COST_130_00)
                .isOccupied(NOT_OCCUPIED)
                .underMaintenance(NOT_UNDER_MAINTENANCE)
                .createdAt(FIXED_INSTANT)
                .updatedAt(FIXED_INSTANT)
                .build();

        given(roomService.updateName(ROOM_ID_1, updateRequest)).willReturn(expectedResponse);

        // when
        ResultActions result = mockMvc.perform(patch(BASE_URL + "/{id}", ROOM_ID_1)
                .contextPath(API_CONTEXT_PATH)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateRequest))
                .with(jwt(ADMIN_USER_ID, ROLE_ADMIN))); // <-- cerrado correctamente aquí

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse)));

        thenMock(roomService).should().updateName(ROOM_ID_1, updateRequest);
    }

}