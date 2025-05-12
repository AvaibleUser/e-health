package org.ehealth.ward.service.or;

import lombok.RequiredArgsConstructor;
import org.ehealth.ward.repository.or.SurgeryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.or.surgery.AddSurgeryDto;
import org.ehealth.ward.domain.dto.or.surgery.SurgeryDto;
import org.ehealth.ward.domain.dto.or.surgery.SurgeryPaymentDto;
import org.ehealth.ward.domain.dto.or.surgery.UpdateSurgeryDto;
import org.ehealth.ward.domain.entity.finance.TariffEntity;
import org.ehealth.ward.domain.entity.or.SurgeryEntity;
import org.ehealth.ward.domain.entity.ward.PatientEntity;
import org.ehealth.ward.domain.exception.ValueNotFoundException;
import org.ehealth.ward.repository.finance.TariffRepository;
import org.ehealth.ward.repository.ward.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
public class SurgeryService implements ISurgeryService {

    private final SurgeryRepository surgeryRepository;
    private final TariffRepository tariffRepository;
    private final PatientRepository patientRepository;

    @Override
    public Optional<SurgeryDto> findByIdAndPatientId(long id, long patientId) {
        return this.surgeryRepository.findByIdAndPatientId(id, patientId, SurgeryDto.class);
    }

    @Override
    public Page<SurgeryDto> findAllByPatientId(long patientId, Pageable pageable) {
        return this.surgeryRepository.findAllByPatientId(patientId, pageable, SurgeryDto.class);
    }

    @Override
    public List<SurgeryPaymentDto> getSurgeryPaymentDto() {
        return this.surgeryRepository.findAllSurgeryPaymentsBySpecialistType();
    }

    @Override
    public boolean existSurgeryPayment(Long specialistId) {
        return this.surgeryRepository.existsById(specialistId);
    }

    @Override
    public void addSurgery(long patientId, AddSurgeryDto surgery) {
        PatientEntity patient = patientRepository.findById(patientId).orElseThrow(
                () -> new ValueNotFoundException("No se encontro el paciente con id " + patientId));

        TariffEntity tariff = tariffRepository.findById(surgery.tariffId())
                .orElseThrow(() -> new ValueNotFoundException(
                        "No se encontro el tipo de paciente con id " + surgery.tariffId()));

        surgeryRepository.save(SurgeryEntity.builder()
                .performedDate(surgery.performedDate())
                .description(surgery.description())
                .patient(patient)
                .tariff(tariff)
                .build());
    }

    @Override
    public void updateSurgery(long surgeryId, long patientId, UpdateSurgeryDto surgery) {
        SurgeryEntity surgeryEntity = surgeryRepository.findByIdAndPatientId(surgeryId, patientId, SurgeryEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la cirugía con id " + surgeryId));

        surgery.performedDate().ifPresent(performedDate -> surgeryEntity.setPerformedDate(performedDate));
        surgery.description().ifPresent(description -> surgeryEntity.setDescription(description));

        surgeryRepository.save(surgeryEntity);
    }
}
