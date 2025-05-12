package org.ehealth.ward.controller.ward;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.ward.admission.AddAdmissionDto;
import org.ehealth.ward.domain.dto.ward.admission.AdmissionDto;
import org.ehealth.ward.domain.dto.ward.admission.UpdateAdmissionDto;
import org.ehealth.ward.service.ward.IAdmissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/patients/{patientId}/admissions")
@RequiredArgsConstructor
public class PatientAdmissionController {

    private final IAdmissionService admissionService;

    @GetMapping("/admitted")
    public List<AdmissionDto> getAdmitted(@PathVariable long patientId) {
        Optional<AdmissionDto> admission = admissionService.findAdmissionByAdmitted(patientId);
        return admission.map(List::of).orElse(List.of());
    }

    @GetMapping("/{admissionId}")
    public ResponseEntity<AdmissionDto> getAdmission(@PathVariable long patientId, @PathVariable long admissionId) {
        return ResponseEntity.of(admissionService.findAdmissionById(patientId, admissionId));
    }

    @GetMapping
    public Page<AdmissionDto> getAdmissions(@PathVariable long patientId, Pageable pageable) {
        return admissionService.findAdmissionsByPatientId(patientId, pageable);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public void registerAdmission(@PathVariable long patientId, @RequestBody AddAdmissionDto admission) {
        admissionService.addAdmission(patientId, admission);
    }

    @PutMapping("/{admissionId}")
    @ResponseStatus(NO_CONTENT)
    public void updateAdmission(@PathVariable long admissionId, @PathVariable long patientId,
            @RequestBody UpdateAdmissionDto admission) {
        admissionService.updateAdmission(admissionId, patientId, admission);
    }
}
