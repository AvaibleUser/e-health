package org.ehealth.ward.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.ward.domain.dto.or.SurgeryPaymentDto;
import org.ehealth.ward.repository.SurgeryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurgeryService implements ISurgeryService {

    private final SurgeryRepository surgeryRepository;

    @Override
    public List<SurgeryPaymentDto> getSurgeryPaymentDto() {
        return this.surgeryRepository.findAllSurgeryPaymentsBySpecialistType();
    }

    @Override
    public boolean existSurgeryPayment(Long specialistId) {
        return this.surgeryRepository.existsById(specialistId);
    }
}
