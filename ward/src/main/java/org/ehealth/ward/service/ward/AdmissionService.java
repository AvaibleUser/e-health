package org.ehealth.ward.service.ward;

import java.time.LocalDate;
import java.util.Optional;

import org.ehealth.ward.domain.dto.ward.admission.AddAdmissionDto;
import org.ehealth.ward.domain.dto.ward.admission.AdmissionDto;
import org.ehealth.ward.domain.entity.ward.AdmissionEntity;
import org.ehealth.ward.domain.entity.ward.AdmissionEntity.AdmissionStatus;
import org.ehealth.ward.domain.entity.ward.PatientEntity;
import org.ehealth.ward.domain.entity.ward.RoomEntity;
import org.ehealth.ward.domain.exception.RequestConflictException;
import org.ehealth.ward.domain.exception.ValueNotFoundException;
import org.ehealth.ward.repository.ward.AdmissionRepository;
import org.ehealth.ward.repository.ward.PatientRepository;
import org.ehealth.ward.repository.ward.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdmissionService implements IAdmissionService {

    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
    private final RoomRepository roomRepository;

    @Override
    public Optional<AdmissionDto> findAdmissionById(long id) {
        return admissionRepository.findById(id, AdmissionDto.class);
    }

    @Override
    public Page<AdmissionDto> findAdmissionsByPatientId(long patientId, Pageable pageable) {
        return admissionRepository.findAllByPatientId(patientId, pageable, AdmissionDto.class);
    }

    @Override
    @Transactional
    public void addAdmission(long patientId, AddAdmissionDto admission) {
        if (admissionRepository.existsByPatientIdAndStatusIsAdmitted(patientId)) {
            throw new RequestConflictException("El paciente ya esta internado");
        }

        PatientEntity patient = patientRepository.findById(patientId).orElseThrow(
                () -> new ValueNotFoundException("No se encontro el paciente con id " + patientId));

        RoomEntity room = roomRepository.findById(admission.roomId())
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la sala con id " + admission.roomId()));

        if (room.isOccupied() || room.isUnderMaintenance()) {
            throw new RequestConflictException("La sala seleccionada ya esta ocupada o en mantenimiento");
        }

        admissionRepository.save(AdmissionEntity.builder()
                .admissionDate(admission.admissionDate())
                .dischargeDate(admission.dischargeDate())
                .patient(patient)
                .room(room)
                .build());
    }

    @Override
    @Transactional
    public void markAsDischarged(long id, long patientId) {
        AdmissionEntity admissionEntity = admissionRepository.findByIdAndPatientId(id, patientId, AdmissionEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el paciente con id " + id));

        if (admissionEntity.getStatus() != AdmissionStatus.ADMITTED) {
            throw new RequestConflictException("El paciente no esta internado");
        }
        admissionEntity.setDischargeDate(LocalDate.now());
        admissionEntity.setStatus(AdmissionStatus.DISCHARGED);

        admissionRepository.save(admissionEntity);
    }
}
