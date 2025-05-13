package org.ehealth.ward.controller.ward;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;

import org.ehealth.ward.domain.dto.ward.patient.AddPatientDto;
import org.ehealth.ward.domain.dto.ward.patient.PatientDto;
import org.ehealth.ward.domain.dto.ward.patient.UpdatePatientDto;
import org.ehealth.ward.service.ward.IPatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final IPatientService patientService;

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable long patientId) {
        return ResponseEntity.of(patientService.findPatientById(patientId));
    }

    @GetMapping
    public Page<PatientDto> getPatients(Pageable pageable, @RequestParam(required = false) String search) {
        return patientService.findPatients(pageable, search);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public void registerPatient(@RequestBody @Valid AddPatientDto patient) {
        patientService.addPatient(patient);
    }

    @PutMapping("/{patientId}")
    @ResponseStatus(NO_CONTENT)
    public void updatePatient(@PathVariable long patientId, @RequestBody @Valid UpdatePatientDto patient) {
        patientService.updatePatient(patientId, patient);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        List<PatientDto> patients = patientService.findAll();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/exist/{id}")
    public boolean existPatientById(@PathVariable Long id) {
        return patientService.existPatientById(id);
    }
}
