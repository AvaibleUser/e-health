package org.ehealth.ward.service.or;

import static org.ehealth.ward.domain.entity.or.SurgerySpecialistEntity.SurgerySpecialistType.DOCTOR;
import static org.ehealth.ward.domain.entity.or.SurgerySpecialistEntity.SurgerySpecialistType.NURSE;
import static org.ehealth.ward.domain.entity.or.SurgerySpecialistEntity.SurgerySpecialistType.SPECIALIST;

import java.util.List;

import org.ehealth.ward.client.EmployeeClient;
import org.ehealth.ward.domain.dto.client.employee.EmployeeDto;
import org.ehealth.ward.domain.dto.or.specialist.CompleteSpecialistDto;
import org.ehealth.ward.domain.dto.or.specialist.SpecialistDto;
import org.ehealth.ward.domain.entity.or.SurgeryEntity;
import org.ehealth.ward.domain.entity.or.SurgerySpecialistEntity;
import org.ehealth.ward.repository.or.SurgeryRepository;
import org.ehealth.ward.repository.or.SurgerySpecialistRepository;
import org.ehealth.ward.repository.ward.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurgerySpecialistService implements ISurgerySpecialistService {

    private final SurgerySpecialistRepository surgerySpecialistRepository;
    private final PatientRepository patientRepository;
    private final SurgeryRepository surgeryRepository;
    private final EmployeeClient employeeClient;

    @Override
    public List<CompleteSpecialistDto> findAssignableSpecialists() {
        return employeeClient.findAssignableSpecialists()
                .stream()
                .map(e -> CompleteSpecialistDto.builder()
                        .employee(e)
                        .specialist(SpecialistDto.builder()
                                .employeeId(e.id())
                                .type(SPECIALIST)
                                .build())
                        .build())
                .toList();
    }

    @Override
    public List<CompleteSpecialistDto> findAssignedEmployees(long patientId, long surgeryId) {
        List<SpecialistDto> assignedEmployees = surgerySpecialistRepository
                .findAllBySurgeryPatientIdAndSurgeryId(patientId, surgeryId, SpecialistDto.class);

        return employeeClient
                .findEmployeesByIds(assignedEmployees.stream().map(SpecialistDto::employeeId).toList())
                .stream()
                .map(e -> CompleteSpecialistDto.builder()
                        .employee(e)
                        .specialist(assignedEmployees.stream()
                                .filter(a -> a.employeeId().equals(e.id()))
                                .findFirst()
                                .orElse(null))
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void assignSpecialists(long patientId, long surgeryId, List<Long> specialistIds) {
        if (!patientRepository.existsById(patientId)) {
            throw new IllegalArgumentException("No se encontro el paciente con id " + patientId);
        }
        SurgeryEntity surgery = surgeryRepository
                .findByIdAndPatientId(surgeryId, patientId, SurgeryEntity.class)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro la cirugía con id " + surgeryId));

        List<EmployeeDto> specialists = employeeClient.findEmployeesByIds(specialistIds);

        if (specialists.stream()
                .filter(e -> e.isSpecialist())
                .count() > 1) {
            throw new IllegalArgumentException("Solo se puede asignar un especialista a la cirugía");
        }

        surgerySpecialistRepository.deleteBySurgeryPatientIdAndSurgeryId(patientId, surgeryId);

        List<SurgerySpecialistEntity> assigned = specialists.stream()
                .map(employee -> SurgerySpecialistEntity.builder()
                        .employeeId(employee.id())
                        .type(employee.isSpecialist() ? SPECIALIST
                                : employee.areaName().equalsIgnoreCase("Medicos") ? DOCTOR : NURSE)
                        .surgery(surgery)
                        .build())
                .toList();

        surgerySpecialistRepository.saveAll(assigned);
    }
}
