package org.ehealth.hr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehealth.hr.domain.dto.AreaResponseDto;
import org.ehealth.hr.domain.dto.CreateAreaDto;
import org.ehealth.hr.domain.dto.UpdateAreaDto;
import org.ehealth.hr.service.AreaService;
import org.ehealth.hr.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.List;

import static org.ehealth.hr.util.JwtBuilder.jwt;
import static org.ehealth.hr.util.ThenMockAlias.thenMock;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(classes = AreaController.class, controllers = AreaController.class)
class AreaControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockitoBean
    protected AreaService areaService;

    private static final Long VALID_AREA_ID = 1L;
    private static final String VALID_AREA_NAME = "Development";
    private static final Instant FIXED_INSTANT = Instant.parse("2024-01-01T00:00:00Z");


    @Test
    void createArea_shouldReturnCreated() throws Exception {
        // given
        CreateAreaDto dto = new CreateAreaDto(VALID_AREA_NAME);
        AreaResponseDto response = AreaResponseDto.builder()
                .id(VALID_AREA_ID)
                .name(VALID_AREA_NAME)
                .createdAt(FIXED_INSTANT)
                .updatedAt(FIXED_INSTANT)
                .build();

        given(areaService.create(dto)).willReturn(response);

        // when
        ResultActions result = mockMvc.perform(post("/api/hr/v1/areas")
                .contextPath("/api/hr")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .with(jwt(1L, "ADMIN")));


        // then
        result.andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(response)));

        thenMock(areaService).should().create(dto);
    }

    @Test
    void findAllAreas_shouldReturnList() throws Exception {
        // given
        List<AreaResponseDto> areas = List.of(
                new AreaResponseDto(1L, "Dev", FIXED_INSTANT, FIXED_INSTANT),
                new AreaResponseDto(2L, "Marketing", FIXED_INSTANT, FIXED_INSTANT)
        );

        given(areaService.findAll()).willReturn(areas);

        // when
        ResultActions result = mockMvc.perform(get("/api/hr/v1/areas")
                .contextPath("/api/hr")
                .with(jwt(1L, "ADMIN")));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(areas)));

        thenMock(areaService).should().findAll();
    }

    @Test
    void updateAreaName_shouldReturnUpdatedArea() throws Exception {
        // given
        UpdateAreaDto dto = new UpdateAreaDto("Updated Area");
        AreaResponseDto response = AreaResponseDto.builder()
                .id(VALID_AREA_ID)
                .name("Updated Area")
                .createdAt(FIXED_INSTANT)
                .updatedAt(FIXED_INSTANT)
                .build();

        given(areaService.updateName(VALID_AREA_ID, dto)).willReturn(response);

        // when
        ResultActions result = mockMvc.perform(patch("/api/hr/v1/areas/{id}", VALID_AREA_ID)
                .contextPath("/api/hr")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .with(jwt(1L, "ADMIN")));


        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));

        thenMock(areaService).should().updateName(VALID_AREA_ID, dto);
    }

}