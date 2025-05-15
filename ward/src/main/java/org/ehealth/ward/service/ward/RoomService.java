package org.ehealth.ward.service.ward;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.ward.room.CreateRoom;
import org.ehealth.ward.domain.dto.ward.room.RoomDto;
import org.ehealth.ward.domain.dto.ward.room.RoomResponseDto;
import org.ehealth.ward.domain.entity.ward.RoomEntity;
import org.ehealth.ward.domain.exception.BusinessException;
import org.ehealth.ward.domain.exception.ResourceNotFoundException;
import org.ehealth.ward.repository.ward.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {

    private final RoomRepository roomRepository;

    @Override
    public Optional<RoomDto> findRoomById(long id) {
        return roomRepository.findById(id, RoomDto.class);
    }

    @Override
    public Page<RoomDto> findRooms(Pageable pageable, boolean onlyAvailable) {
        if (onlyAvailable) {
            return roomRepository.findAllFiltered(pageable);
        }
        return roomRepository.findAllBy(pageable, RoomDto.class);
    }

    @Override
    public RoomResponseDto create(CreateRoom dto) {
        if (roomRepository.existsByNumberIgnoreCase(dto.number())) {
            throw new BusinessException("El numero de habitacion ya existe");
        }

        var area = RoomEntity.builder()
                .number(dto.number().trim())
                .costPerDay(dto.costPerDay())
                .isOccupied(false)
                .underMaintenance(false)
                .build();

        return RoomResponseDto.fromEntity(roomRepository.save(area));
    }

    @Override
    public List<RoomResponseDto> findAll() {
        return roomRepository.findAll().stream()
                .map(RoomResponseDto::fromEntity)
                .toList();
    }

    @Override
    public RoomResponseDto updateName(Long id, CreateRoom dto) {
        var area = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion no encontrada con el id " + id));

        if (!area.getNumber().equalsIgnoreCase(dto.number()) && roomRepository.existsByNumberIgnoreCase(dto.number())) {
            throw new BusinessException("El Numero de Habitacion ya existe");
        }

        area.setNumber(dto.number().trim());
        area.setCostPerDay(dto.costPerDay());
        return RoomResponseDto.fromEntity(roomRepository.save(area));
    }



}
