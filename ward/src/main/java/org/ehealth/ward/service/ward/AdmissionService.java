package org.ehealth.ward.service.ward;

import static org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType.HOSPITALIZED;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.ehealth.ward.domain.dto.ward.admission.AddAdmissionDto;
import org.ehealth.ward.domain.dto.ward.admission.AdmissionDto;
import org.ehealth.ward.domain.dto.ward.admission.UpdateAdmissionDto;
import org.ehealth.ward.domain.entity.finance.BillEntity;
import org.ehealth.ward.domain.entity.finance.BillItemEntity;
import org.ehealth.ward.domain.entity.ward.AdmissionEntity;
import org.ehealth.ward.domain.entity.ward.PatientEntity;
import org.ehealth.ward.domain.entity.ward.RoomEntity;
import org.ehealth.ward.domain.exception.RequestConflictException;
import org.ehealth.ward.domain.exception.ValueNotFoundException;
import org.ehealth.ward.repository.finance.BillItemRepository;
import org.ehealth.ward.repository.finance.BillRepository;
import org.ehealth.ward.repository.ward.AdmissionRepository;
import org.ehealth.ward.repository.ward.PatientRepository;
import org.ehealth.ward.repository.ward.RoomRepository;
import org.hibernate.Hibernate;
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
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;

    @Override
    public Optional<AdmissionDto> findAdmissionByAdmitted(long patientId) {
        return admissionRepository.findByPatientIdAndIsAdmitted(patientId);
    }

    @Override
    public Optional<AdmissionDto> findAdmissionById(long patientId, long admissionId) {
        return admissionRepository.findByIdAndPatientId(admissionId, patientId, AdmissionDto.class);
    }

    @Override
    public Page<AdmissionDto> findAdmissionsByPatientId(long patientId, Pageable pageable) {
        return admissionRepository.findAllByPatientId(patientId, pageable, AdmissionDto.class);
    }

    @Override
    @Transactional
    public void addAdmission(long patientId, AddAdmissionDto admission) {
        admissionRepository.findByPatientIdAndIsAdmitted(patientId).ifPresent(admissionEntity -> {
            throw new RequestConflictException("El paciente ya esta internado");
        });

        PatientEntity patient = patientRepository.findById(patientId).orElseThrow(
                () -> new ValueNotFoundException("No se encontro el paciente con id " + patientId));

        RoomEntity room = roomRepository.findById(admission.roomId())
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la sala con id " + admission.roomId()));

        Hibernate.initialize(room.getAdmissions());
        if (room.getAdmissions()
                .stream()
                .map(AdmissionEntity::getDischargeDate)
                .anyMatch(date -> date == null || date.isAfter(LocalDate.now()))) {
            throw new RequestConflictException("La sala seleccionada ya esta ocupada");
        }
        if (room.isUnderMaintenance()) {
            throw new RequestConflictException("La sala seleccionada esta en mantenimiento");
        }

        AdmissionEntity dbAdmission = AdmissionEntity.builder()
                .admissionDate(admission.admissionDate())
                .dischargeDate(admission.dischargeDate())
                .patient(patient)
                .room(room)
                .build();

        BillEntity bill = billRepository.findByPatientIdAndIsClosedFalse(patientId, BillEntity.class)
                .orElseGet(() -> {
                    BillEntity dbBill = BillEntity.builder()
                            .patient(patient)
                            .isClosed(true)
                            .isPaid(true)
                            .build();

                    billRepository.save(dbBill);
                    return dbBill;
                });

        BillItemEntity item = BillItemEntity.builder()
                .concept("Hospitalización en el cuarto '" + room.getNumber() + "' el " + admission.admissionDate())
                .amount(BigDecimal.ONE)
                .type(HOSPITALIZED)
                .bill(bill)
                .admission(dbAdmission)
                .build();

        billItemRepository.save(item);
    }

    @Override
    @Transactional
    public void updateAdmission(long id, long patientId, UpdateAdmissionDto admission) {
        Long currentAdmissionId = admissionRepository
                .findByPatientIdAndIsAdmitted(patientId)
                .map(AdmissionDto::id)
                .orElseThrow(() -> new ValueNotFoundException("El paciente no esta internado"));

        if (currentAdmissionId != id) {
            throw new ValueNotFoundException("La hospitalizacion ya ha sido finalizada");
        }
        AdmissionEntity admissionEntity = admissionRepository.findByIdAndPatientId(id, patientId, AdmissionEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la hospitalizacion con id " + id));

        admission.dischargeDate().ifPresent(dischargeDate -> admissionEntity.setDischargeDate(dischargeDate));
        admission.roomId()
                .map(roomId -> {
                    RoomEntity room = roomRepository.findById(roomId)
                            .orElseThrow(() -> new ValueNotFoundException("No se encontro la sala con id " + roomId));

                    Hibernate.initialize(room.getAdmissions());
                    if (room.getAdmissions()
                            .stream()
                            .map(AdmissionEntity::getDischargeDate)
                            .anyMatch(date -> date == null || date.isAfter(LocalDate.now()))) {
                        throw new RequestConflictException("La sala seleccionada ya esta ocupada");
                    }
                    if (room.isUnderMaintenance()) {
                        throw new RequestConflictException("La sala seleccionada esta en mantenimiento");
                    }
                    return room;
                })
                .ifPresent(admissionEntity::setRoom);

        admissionRepository.save(admissionEntity);
    }
}
