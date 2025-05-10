package org.ehealth.ward.service.ward;

import java.util.Optional;

import org.ehealth.ward.domain.dto.ward.room.RoomDto;
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
}
