package org.ehealth.hr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.dto.FinishContractDto;
import org.ehealth.hr.domain.dto.vacation.CreateRequestVacationDto;
import org.ehealth.hr.domain.dto.vacation.UpdateRequestVacationDto;
import org.ehealth.hr.domain.dto.vacation.VacationPendingDto;
import org.ehealth.hr.service.IVacationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/vacations")
@RequiredArgsConstructor
public class VacationController {

    private final IVacationService vacationService;


    @GetMapping("/pending")
    public ResponseEntity<List<VacationPendingDto>> getAllPendingVacations() {
        List<VacationPendingDto> pendingVacations = vacationService.findAllPendingVacations();
        return ResponseEntity.ok(pendingVacations);
    }

    @PatchMapping("/update-state/{vacationId}")
    public ResponseEntity<List<VacationPendingDto>> updatePendingVacations(@PathVariable Long vacationId, @RequestBody @Valid UpdateRequestVacationDto updateRequestVacationDto) {
        List<VacationPendingDto> pendingVacations = vacationService.updatePendingVacations(vacationId,updateRequestVacationDto);
        return ResponseEntity.ok(pendingVacations);
    }

    @PostMapping("/request")
    public ResponseEntity<VacationPendingDto> createVacationRequest(@RequestBody CreateRequestVacationDto dto) {
        VacationPendingDto response = vacationService.createRequestVacation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/approved")
    public ResponseEntity<List<VacationPendingDto>> getLastApprovedVacationsPerEmployee() {
        List<VacationPendingDto> vacations = vacationService.findAllApprovedVacations();
        return ResponseEntity.ok(vacations);
    }

}
