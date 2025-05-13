package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.or.PaymentPerSurgeryDto;
import org.ehealth.hr.domain.dto.or.SurgeryPaymentDto;
import org.ehealth.hr.domain.dto.reports.PaymentEmployeeDto;
import org.ehealth.hr.domain.dto.reports.ReportExpensePayEmployeeDto;

import java.time.LocalDate;
import java.util.List;

public interface ISpecialistPaymentService {

    List<PaymentPerSurgeryDto> getPaymentPerSurgery();

    void createPaymentPerSurgery(PaymentPerSurgeryDto surgeryPaymentDto);

    ReportExpensePayEmployeeDto getReportPayEmployeeInRange(LocalDate startDate, LocalDate endDate);

    ReportExpensePayEmployeeDto getReportPayEmployee(List<PaymentEmployeeDto> items);
}
