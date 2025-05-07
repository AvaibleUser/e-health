package org.ehealth.ward.service;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.ehealth.ward.domain.dto.patient.AddPatientDto;
import org.ehealth.ward.domain.dto.patient.PatientDto;
import org.ehealth.ward.domain.dto.patient.UpdatePatientDto;
import org.ehealth.ward.domain.entity.ward.PatientEntity;
import org.ehealth.ward.domain.exception.RequestConflictException;
import org.ehealth.ward.domain.exception.ValueNotFoundException;
import org.ehealth.ward.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService implements IPatientService {

    private final PatientRepository patientRepository;

    @Override
    public Page<PatientDto> findPatients(Pageable pageable, String filter) {
        if (StringUtils.isNotBlank(filter)) {
            return patientRepository.findAllWithSearch(filter, pageable);
        }
        return patientRepository.findAllPageableBy(pageable, PatientDto.class);
    }

    @Override
    public Optional<PatientDto> findPatientById(long id) {
        return patientRepository.findById(id, PatientDto.class);
    }

    @Override
    @Transactional
    public void addPatient(AddPatientDto patient) {
        if (patientRepository.existsByCui(patient.cui())) {
            throw new RequestConflictException("El CUI que se intenta registrar ya esta en uso");
        }
        patientRepository.save(PatientEntity.builder()
                .fullName(patient.fullName())
                .cui(patient.cui())
                .birthDate(patient.birthDate())
                .phone(patient.phone())
                .email(patient.email())
                .build());
    }

    @Override
    @Transactional
    public void updatePatient(long id, UpdatePatientDto newPatient) {
        PatientEntity patientEntity = patientRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el paciente con id " + id));

        newPatient.fullName().filter(StringUtils::isNotBlank).ifPresent(patientEntity::setFullName);
        newPatient.dateOfBirth().ifPresentOrElse(patientEntity::setBirthDate, () -> patientEntity.setBirthDate(null));
        newPatient.phone().ifPresentOrElse(patientEntity::setPhone, () -> patientEntity.setPhone(null));
        newPatient.email().ifPresentOrElse(patientEntity::setEmail, () -> patientEntity.setEmail(null));

        patientRepository.save(patientEntity);
    }
}
