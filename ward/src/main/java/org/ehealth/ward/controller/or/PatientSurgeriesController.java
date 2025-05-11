package org.ehealth.ward.controller.or;

import org.ehealth.ward.domain.dto.or.surgery.AddSurgeryDto;
import org.ehealth.ward.domain.dto.or.surgery.SurgeryDto;
import org.ehealth.ward.domain.dto.or.surgery.UpdateSurgeryDto;
import org.ehealth.ward.service.or.ISurgeryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/patients/{patientId}/surgeries")
@RequiredArgsConstructor
public class PatientSurgeriesController {

    private final ISurgeryService surgeryService;

    @GetMapping
    public Page<SurgeryDto> findAllByPatientId(@PathVariable long patientId, Pageable pageable) {
        return surgeryService.findAllByPatientId(patientId, pageable);
    }

    @GetMapping("/{surgeryId}")
    public ResponseEntity<SurgeryDto> findByIdAndPatientId(@PathVariable long surgeryId, @PathVariable long patientId) {
        return ResponseEntity.of(surgeryService.findByIdAndPatientId(surgeryId, patientId));
    }

    @PostMapping
    public void addSurgery(@RequestBody AddSurgeryDto surgery, @PathVariable long patientId) {
        surgeryService.addSurgery(patientId, surgery);
    }

    @PutMapping("/{surgeryId}")
    public void updateSurgery(@RequestBody UpdateSurgeryDto surgery, @PathVariable long surgeryId,
            @PathVariable long patientId) {
        surgeryService.updateSurgery(surgeryId, patientId, surgery);
    }
}
