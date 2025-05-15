package org.ehealth.ward.controller.ward;

import jakarta.validation.Valid;
import org.ehealth.ward.domain.dto.ward.room.CreateRoom;
import org.ehealth.ward.domain.dto.ward.room.RoomDto;
import org.ehealth.ward.domain.dto.ward.room.RoomResponseDto;
import org.ehealth.ward.service.ward.IRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

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

    @PostMapping()
    public ResponseEntity<RoomResponseDto> create(@Valid @RequestBody CreateRoom dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoomResponseDto>> findAll() {
        return ResponseEntity.ok(roomService.findAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RoomResponseDto> updateName(@PathVariable Long id, @Valid @RequestBody CreateRoom dto) {
        return ResponseEntity.ok(roomService.updateName(id, dto));
    }
}
