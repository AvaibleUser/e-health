package org.ehealth.ward.service.or;

import java.util.List;

import org.ehealth.ward.domain.dto.or.specialist.CompleteSpecialistDto;

public interface ISurgerySpecialistService {

    List<CompleteSpecialistDto> findAssignableSpecialists();

    List<CompleteSpecialistDto> findAssignedEmployees(long patientId, long surgeryId);

    void assignSpecialists(long patientId, long surgeryId, List<Long> specialistIds);
}
