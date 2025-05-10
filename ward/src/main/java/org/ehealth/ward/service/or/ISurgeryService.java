package org.ehealth.ward.service.or;

import org.ehealth.ward.domain.dto.or.SurgeryPaymentDto;

import java.util.List;

public interface ISurgeryService {
    List<SurgeryPaymentDto> getSurgeryPaymentDto();
    boolean existSurgeryPayment(Long specialistId);

}
