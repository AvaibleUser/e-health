package org.ehealth.hr.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.ehealth.hr.client.PatientClient;
import org.ehealth.hr.domain.dto.EmployeeDto;
import org.ehealth.hr.domain.dto.or.PaymentPerSurgeryDto;
import org.ehealth.hr.domain.dto.or.SpecialistPaymentDto;
import org.ehealth.hr.domain.dto.or.SurgeryPaymentDto;
import org.ehealth.hr.domain.dto.reports.PaymentEmployeeDto;
import org.ehealth.hr.domain.dto.reports.ReportExpensePayEmployeeDto;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.entity.SpecialistPaymentEntity;
import org.ehealth.hr.domain.exception.RequestConflictException;
import org.ehealth.hr.domain.exception.ValueNotFoundException;
import org.ehealth.hr.repository.EmployeeRepository;
import org.ehealth.hr.repository.SpecialistPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialistPaymentService implements ISpecialistPaymentService {

    private final SpecialistPaymentRepository specialistPaymentRepository;
    private final PatientClient patientClient;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<PaymentPerSurgeryDto> getPaymentPerSurgery() {

        List<SurgeryPaymentDto> surgeryPaymentDtos;
        try {
            surgeryPaymentDtos = this.patientClient.getSurgeryPayments();
        } catch (FeignException e) {
            throw new RequestConflictException("No se ha podido obtener las cirugias, intente mas tarde");
        }

        List<EmployeeDto> employeeDtos = this.employeeRepository.findAllByIsSpecialistTrueOrderByCreatedAtDesc(EmployeeDto.class);
        List<SpecialistPaymentDto> specialistPaymentDtos = this.specialistPaymentRepository.findAllSpecialistPayments();

        List<PaymentPerSurgeryDto> paymentPerSurgeryDtos = new ArrayList<>();

        // Crea un set para búsqueda rápida de combinaciones ya pagadas
        Set<String> paidCombinations = specialistPaymentDtos.stream()
                .map(p -> p.surgeryId() + "-" + p.employeeId())
                .collect(Collectors.toSet());

        for (SurgeryPaymentDto surgery : surgeryPaymentDtos) {
            String key = surgery.id() + "-" + surgery.employeeId();
            if (!paidCombinations.contains(key)) {
                // Buscar el empleado
                employeeDtos.stream()
                        .filter(e -> e.id().equals(surgery.employeeId()))
                        .findFirst().ifPresent(employee -> paymentPerSurgeryDtos.add(PaymentPerSurgeryDto.builder()
                                .specialistFee(surgery.specialistFee())
                                .id(surgery.id())
                                .description(surgery.description())
                                .performedDate(surgery.performedDate())
                                .employeeId(employee.id())
                                .fullName(employee.fullName())
                                .Cui(employee.cui())
                                .build()));

            }
        }

        return paymentPerSurgeryDtos;
    }

    @Override
    @Transactional
    public void createPaymentPerSurgery(PaymentPerSurgeryDto surgeryPaymentDto) {
        EmployeeEntity employee = this.employeeRepository.findById(surgeryPaymentDto.employeeId())
                .orElseThrow(() -> new ValueNotFoundException("Empleado especialista no encontrado para el pago"));

        boolean exist;

        try {
            exist = this.patientClient.existSurge(surgeryPaymentDto.id());
        } catch (FeignException e) {
            throw new RequestConflictException("No se ha podido encontrar la cirugia que intenta pagar, intente mas tarde");
        }

        if (!exist){
            throw new ValueNotFoundException("La cirugia que intenta pagar no existe");
        }

        SpecialistPaymentEntity paymentEntity = SpecialistPaymentEntity
                .builder()
                .surgeryId(surgeryPaymentDto.id())
                .amount(surgeryPaymentDto.specialistFee())
                .specialistDoctor(employee)
                .build();

        this.specialistPaymentRepository.save(paymentEntity);
    }

    @Override
    public ReportExpensePayEmployeeDto getReportPayEmployee(List<PaymentEmployeeDto> items){
        if (items == null || items.isEmpty()) {
            return ReportExpensePayEmployeeDto.builder()
                    .totalAmount(BigDecimal.ZERO)
                    .items(Collections.emptyList())
                    .build();
        }

        BigDecimal totalIncome = items.stream()
                .map(PaymentEmployeeDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportExpensePayEmployeeDto.builder()
                .totalAmount(totalIncome)
                .items(items)
                .build();

    }

    @Override
    public ReportExpensePayEmployeeDto getReportPayEmployeeInRange(LocalDate startDate, LocalDate endDate) {
        List<PaymentEmployeeDto> items;

        if (startDate == null || endDate == null) {
            items = this.specialistPaymentRepository.findAllPaymentsProjected();
            return this.getReportPayEmployee(items);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        items = specialistPaymentRepository.findAllPaymentsInRange(startInstant,endInstant);

        return this.getReportPayEmployee(items);

    }

}
