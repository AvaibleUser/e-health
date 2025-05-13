package org.ehealth.hr.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.dto.ContractDto;
import org.ehealth.hr.domain.dto.FinishContractDto;
import org.ehealth.hr.domain.dto.NewContractDto;
import org.ehealth.hr.domain.dto.UpdateSalaryDto;
import org.ehealth.hr.domain.dto.reports.ReportEmployeeContracts;
import org.ehealth.hr.service.IContractService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/history/employee/{employeeId}")
    public ResponseEntity<List<ContractDto>> getAllContracts(@PathVariable Long employeeId) {
        List<ContractDto> contracts = contractService.findAllContractsOrderedByCreationDate(employeeId);
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("reports/employees/history/{areaId}")
    public ResponseEntity<ReportEmployeeContracts> getEmployeeContractReport(
            @PathVariable Long areaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {

        ReportEmployeeContracts report = contractService.reportEmployeeContracts(areaId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("reports/employees/history/terminated/{areaId}")
    public ResponseEntity<ReportEmployeeContracts> getReportTerminatedContracts(
            @PathVariable Long areaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {

        ReportEmployeeContracts report = contractService.reportTerminatedContracts(areaId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

}
