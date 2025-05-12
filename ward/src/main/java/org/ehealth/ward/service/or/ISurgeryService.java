package org.ehealth.ward.service.or;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.or.surgery.AddSurgeryDto;
import org.ehealth.ward.domain.dto.or.surgery.SurgeryDto;
import org.ehealth.ward.domain.dto.or.surgery.SurgeryPaymentDto;
import org.ehealth.ward.domain.dto.or.surgery.UpdateSurgeryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISurgeryService {

    Optional<SurgeryDto> findByIdAndPatientId(long id, long patientId);

    Page<SurgeryDto> findAllByPatientId(long patientId, Pageable pageable);

    void addSurgery(long patientId, AddSurgeryDto surgery);

    void updateSurgery(long surgeryId, long patientId, UpdateSurgeryDto surgery);

    List<SurgeryPaymentDto> getSurgeryPaymentDto();

    boolean existSurgeryPayment(Long specialistId);

}
