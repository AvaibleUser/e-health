package org.ehealth.ward.controller.ward;

import org.ehealth.ward.domain.dto.ward.room.RoomDto;
import org.ehealth.ward.service.ward.IRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final IRoomService roomService;

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoom(@PathVariable long roomId) {
        return ResponseEntity.of(roomService.findRoomById(roomId));
    }

    @GetMapping
    public Page<RoomDto> getRooms(Pageable pageable, @RequestParam(defaultValue = "false") boolean onlyAvailable) {
        return roomService.findRooms(pageable, onlyAvailable);
    }
}
