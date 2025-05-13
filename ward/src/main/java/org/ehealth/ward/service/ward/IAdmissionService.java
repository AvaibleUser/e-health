package org.ehealth.ward.service.ward;

import java.util.Optional;

import org.ehealth.ward.domain.dto.ward.admission.AddAdmissionDto;
import org.ehealth.ward.domain.dto.ward.admission.AdmissionDto;
import org.ehealth.ward.domain.dto.ward.admission.UpdateAdmissionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAdmissionService {

    Optional<AdmissionDto> findAdmissionByAdmitted(long patientId);

    Optional<AdmissionDto> findAdmissionById(long patientId, long admissionId);

    Page<AdmissionDto> findAdmissionsByPatientId(long patientId, Pageable pageable);

    void addAdmission(long patientId, AddAdmissionDto admission);

    void updateAdmission(long id, long patientId, UpdateAdmissionDto admission);
}
