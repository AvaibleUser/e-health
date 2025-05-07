package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.or.PaymentPerSurgeryDto;
import org.ehealth.hr.domain.dto.or.SurgeryPaymentDto;

import java.util.List;

public interface ISpecialistPaymentService {

    List<PaymentPerSurgeryDto> getPaymentPerSurgery();

    void createPaymentPerSurgery(PaymentPerSurgeryDto surgeryPaymentDto);
}
