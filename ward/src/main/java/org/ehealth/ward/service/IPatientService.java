package org.ehealth.ward.service;

import java.util.Optional;

import org.ehealth.ward.domain.dto.patient.AddPatientDto;
import org.ehealth.ward.domain.dto.patient.PatientDto;
import org.ehealth.ward.domain.dto.patient.UpdatePatientDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPatientService {

    Page<PatientDto> findPatients(Pageable pageable, String filter);

    Optional<PatientDto> findPatientById(long id);

    void addPatient(AddPatientDto patient);

    void updatePatient(long id, UpdatePatientDto patient);
}
