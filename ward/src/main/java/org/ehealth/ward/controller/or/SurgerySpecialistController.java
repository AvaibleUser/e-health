package org.ehealth.ward.controller.or;

import java.util.List;

import org.ehealth.ward.domain.dto.or.specialist.CompleteSpecialistDto;
import org.ehealth.ward.service.or.ISurgerySpecialistService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/surgery-specialists")
@RequiredArgsConstructor
public class SurgerySpecialistController {

    private final ISurgerySpecialistService assignedSpecialistService;

    @GetMapping("/assignable")
    public List<CompleteSpecialistDto> findAssignableSpecialists() {
        return assignedSpecialistService.findAssignableSpecialists();
    }
}
