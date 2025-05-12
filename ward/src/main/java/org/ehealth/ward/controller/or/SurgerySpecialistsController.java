package org.ehealth.ward.controller.or;

import java.util.List;

import org.ehealth.ward.domain.dto.or.specialist.CompleteSpecialistDto;
import org.ehealth.ward.service.or.ISurgerySpecialistService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/patients/{patientId}/surgeries/{surgeryId}/specialists")
@RequiredArgsConstructor
public class SurgerySpecialistsController {

    private final ISurgerySpecialistService assignedSpecialistService;

    @GetMapping
    public List<CompleteSpecialistDto> getAssignedSpecialists(@PathVariable long patientId,
            @PathVariable long surgeryId) {
        return assignedSpecialistService.findAssignedEmployees(patientId, surgeryId);
    }

    @PutMapping
    public void assignSpecialists(@PathVariable long patientId, @PathVariable long surgeryId,
            @RequestBody List<Long> specialistIds) {
        assignedSpecialistService.assignSpecialists(patientId, surgeryId, specialistIds);
    }
}
