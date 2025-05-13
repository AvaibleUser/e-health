package org.ehealth.hr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.dto.AreaResponseDto;
import org.ehealth.hr.domain.dto.CreateAreaDto;
import org.ehealth.hr.domain.dto.UpdateAreaDto;
import org.ehealth.hr.service.IAreaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/areas")
@RequiredArgsConstructor
public class AreaController {
    private final IAreaService areaService;

    @PostMapping()
    public ResponseEntity<AreaResponseDto> create(@Valid @RequestBody CreateAreaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(areaService.create(dto));
    }

    @GetMapping()
    public ResponseEntity<List<AreaResponseDto>> findAll() {
        return ResponseEntity.ok(areaService.findAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AreaResponseDto> updateName(@PathVariable Long id, @Valid @RequestBody UpdateAreaDto dto) {
        return ResponseEntity.ok(areaService.updateName(id, dto));
    }

}
