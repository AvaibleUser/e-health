package org.ehealth.hr.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.dto.AreaResponseDto;
import org.ehealth.hr.domain.dto.CreateAreaDto;
import org.ehealth.hr.domain.dto.UpdateAreaDto;
import org.ehealth.hr.domain.entity.AreaEntity;
import org.ehealth.hr.domain.exception.BusinessException;
import org.ehealth.hr.domain.exception.ResourceNotFoundException;
import org.ehealth.hr.repository.AreaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaService implements IAreaService {
    private final AreaRepository areaRepository;

    @Override
    public AreaResponseDto create(CreateAreaDto dto) {
        if (areaRepository.existsByNameIgnoreCase(dto.name())) {
            throw new BusinessException("El area ya existe");
        }

        var area = AreaEntity.builder()
                .name(dto.name().trim())
                .build();

        return AreaResponseDto.fromEntity(areaRepository.save(area));
    }

    @Override
    public List<AreaResponseDto> findAll() {
        return areaRepository.findAll().stream()
                .map(AreaResponseDto::fromEntity)
                .toList();
    }

    @Override
    public AreaResponseDto updateName(Long id, UpdateAreaDto dto) {
        var area = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area no encontrada con el id " + id));

        if (areaRepository.existsByNameIgnoreCase(dto.newName())) {
            throw new BusinessException("El area con ese nombre ya existe");
        }

        area.setName(dto.newName().trim());
        return AreaResponseDto.fromEntity(areaRepository.save(area));
    }

}
