package org.ehealth.ward.service.ward;

import java.util.Optional;

import org.ehealth.ward.domain.dto.ward.room.RoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRoomService {

    Optional<RoomDto> findRoomById(long id);

    Page<RoomDto> findRooms(Pageable pageable, boolean onlyAvailable);
}
