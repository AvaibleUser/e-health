package org.ehealth.ward.service.ward;

import static org.ehealth.ward.domain.entity.ward.AssignedEmployeeEntity.AssignedEmployeeType.DOCTOR;
import static org.ehealth.ward.domain.entity.ward.AssignedEmployeeEntity.AssignedEmployeeType.NURSE;

import java.time.LocalDate;
import java.util.List;

import org.ehealth.ward.client.EmployeeClient;
import org.ehealth.ward.domain.dto.client.employee.EmployeeDto;
import org.ehealth.ward.domain.dto.ward.admission.AdmissionDto;
import org.ehealth.ward.domain.dto.ward.employee.AssignedEmployeeDto;
import org.ehealth.ward.domain.dto.ward.employee.AssignedEmployeeReportDto;
import org.ehealth.ward.domain.dto.ward.employee.CompleteEmployeeDto;
import org.ehealth.ward.domain.entity.ward.AdmissionEntity;
import org.ehealth.ward.domain.entity.ward.AssignedEmployeeEntity;
import org.ehealth.ward.domain.exception.RequestConflictException;
import org.ehealth.ward.domain.exception.ValueNotFoundException;
import org.ehealth.ward.repository.ward.AdmissionRepository;
import org.ehealth.ward.repository.ward.AssignedEmployeeRepository;
import org.ehealth.ward.repository.ward.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignedEmployeeService implements IAssignedEmployeeService {

    private final AssignedEmployeeRepository assignedEmployeeRepository;
    private final PatientRepository patientRepository;
    private final AdmissionRepository admissionRepository;
    private final EmployeeClient employeeClient;

    @Override
    public List<AssignedEmployeeReportDto> getAssignedDoctorsReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return this.assignedEmployeeRepository.findALLDoctorsAssigned();
        }
        return assignedEmployeeRepository.findDoctorsAssignedInPeriod(startDate, endDate);
    }

    @Override
    public List<CompleteEmployeeDto> getAssignableEmployees() {
        return employeeClient.findAssignableEmployees()
                .stream()
                .map(employee -> CompleteEmployeeDto.builder()
                        .employee(employee)
                        .assignedEmployee(AssignedEmployeeDto.builder()
                                .employeeId(employee.id())
                                .type(employee.areaName().equalsIgnoreCase("Medicos") ? DOCTOR : NURSE)
                                .build())
                        .build())
                .toList();
    }

    @Override
    public List<CompleteEmployeeDto> getAssignedEmployees(long patientId, long admissionId) {
        List<AssignedEmployeeDto> assignedEmployees = assignedEmployeeRepository
                .findByAdmissionPatientIdAndAdmissionId(patientId, admissionId, AssignedEmployeeDto.class);

        return employeeClient
                .findEmployeesByIds(assignedEmployees.stream().map(AssignedEmployeeDto::employeeId).toList())
                .stream()
                .map(employee -> CompleteEmployeeDto.builder()
                        .employee(employee)
                        .assignedEmployee(assignedEmployees.stream()
                                .filter(a -> a.employeeId().equals(employee.id()))
                                .findAny()
                                .orElse(null))
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void assignEmployees(long patientId, long admissionId, List<Long> employeeIds) {
        if (!patientRepository.existsById(patientId)) {
            throw new RequestConflictException("El paciente no existe");
        }
        Long currentAdmissionId = admissionRepository
                .findByPatientIdAndIsAdmitted(patientId)
                .map(AdmissionDto::id)
                .orElseThrow(() -> new ValueNotFoundException("El paciente no esta internado"));

        if (currentAdmissionId != admissionId) {
            throw new ValueNotFoundException("La hospitalizacion ya ha sido finalizada");
        }
        AdmissionEntity admission = admissionRepository
                .findByIdAndPatientId(admissionId, patientId, AdmissionEntity.class)
                .orElseThrow(
                        () -> new ValueNotFoundException("No se encontro la hospitalizacion con id " + admissionId));

        List<EmployeeDto> employees = employeeClient.findEmployeesByIds(employeeIds);

        assignedEmployeeRepository.deleteByAdmissionPatientIdAndAdmissionId(patientId, admissionId);

        List<AssignedEmployeeEntity> assigned = employees.stream()
                .map(employee -> AssignedEmployeeEntity.builder()
                        .employeeId(employee.id())
                        .type(employee.areaName().equalsIgnoreCase("Medicos") ? DOCTOR : NURSE)
                        .admission(admission)
                        .build())
                .toList();

        assignedEmployeeRepository.saveAll(assigned);
    }
}

