package org.ehealth.ward.service.ward;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.ward.room.CreateRoom;
import org.ehealth.ward.domain.dto.ward.room.RoomDto;
import org.ehealth.ward.domain.dto.ward.room.RoomResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRoomService {

    Optional<RoomDto> findRoomById(long id);

    Page<RoomDto> findRooms(Pageable pageable, boolean onlyAvailable);
    RoomResponseDto create(CreateRoom dto);
    List<RoomResponseDto> findAll();
    RoomResponseDto updateName(Long id, CreateRoom dto);
}
