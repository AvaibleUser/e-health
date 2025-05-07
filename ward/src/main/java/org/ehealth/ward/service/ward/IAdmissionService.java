package org.ehealth.ward.service.ward;

import java.util.Optional;

import org.ehealth.ward.domain.dto.ward.admission.AddAdmissionDto;
import org.ehealth.ward.domain.dto.ward.admission.AdmissionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAdmissionService {

    Optional<AdmissionDto> findAdmissionById(long id);

    Page<AdmissionDto> findAdmissionsByPatientId(long patientId, Pageable pageable);

    void addAdmission(long patientId, AddAdmissionDto admission);

    void markAsDischarged(long id, long patientId);
}
