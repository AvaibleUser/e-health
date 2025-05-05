package org.ehealth.hr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.dto.ContractDto;
import org.ehealth.hr.domain.dto.FinishContractDto;
import org.ehealth.hr.domain.dto.NewContractDto;
import org.ehealth.hr.domain.dto.UpdateSalaryDto;
import org.ehealth.hr.service.IContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final IContractService contractService;

    @GetMapping("/latest/employee/{employeeId}")
    public ResponseEntity<ContractDto> getContractByEmployeeId(@PathVariable Long employeeId) {
        ContractDto latestContract = contractService.getContractByEmployeeId(employeeId);
        return ResponseEntity.ok(latestContract);
    }

    @PostMapping
    public ResponseEntity<Void> createNewContract(@RequestBody @Valid NewContractDto newContractDto) {
        contractService.createNewContract(newContractDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/finish/{contractId}")
    public ResponseEntity<Void> finishContract(@PathVariable Long contractId, @RequestBody @Valid FinishContractDto finishContractDto) {
        contractService.finishContract(contractId,finishContractDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/update-salary/{contractId}")
    public ResponseEntity<Void> updateSalary(@PathVariable Long contractId, @RequestBody @Valid UpdateSalaryDto updateSalaryDto) {
        contractService.updateSalary(contractId,updateSalaryDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/dismissal-work/{contractId}")
    public ResponseEntity<Void> dismissalWork(@PathVariable Long contractId, @RequestBody @Valid FinishContractDto finishContractDto) {
        contractService.dismissalWork(contractId,finishContractDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
