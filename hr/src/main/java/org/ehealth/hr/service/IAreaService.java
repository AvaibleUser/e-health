package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.AreaResponseDto;
import org.ehealth.hr.domain.dto.CreateAreaDto;
import org.ehealth.hr.domain.dto.UpdateAreaDto;

import java.util.List;

public interface IAreaService {
    AreaResponseDto create(CreateAreaDto dto);
    List<AreaResponseDto> findAll();
    AreaResponseDto updateName(Long id, UpdateAreaDto dto);
}
