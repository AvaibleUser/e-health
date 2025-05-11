package org.ehealth.rx.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ehealth.rx.domain.dto.CreateMedicineDto;
import org.ehealth.rx.domain.dto.MedicineDto;
import org.ehealth.rx.service.IMedicineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final IMedicineService medicineService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateMedicineDto dto) {
        medicineService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<MedicineDto>> findAllEmployees() {
        List<MedicineDto> medicines = medicineService.findAll();
        return ResponseEntity.ok(medicines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineDto> findById(@PathVariable Long id) {
        MedicineDto medicine = medicineService.findById(id);
        return ResponseEntity.ok(medicine);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> findById(@PathVariable Long id, @Valid @RequestBody CreateMedicineDto dto) {
        this.medicineService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
