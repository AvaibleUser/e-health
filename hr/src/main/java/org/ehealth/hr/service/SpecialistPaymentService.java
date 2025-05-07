package org.ehealth.hr.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.ehealth.hr.client.SurgeryClient;
import org.ehealth.hr.domain.dto.EmployeeDto;
import org.ehealth.hr.domain.dto.or.PaymentPerSurgeryDto;
import org.ehealth.hr.domain.dto.or.SpecialistPaymentDto;
import org.ehealth.hr.domain.dto.or.SurgeryPaymentDto;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.entity.SpecialistPaymentEntity;
import org.ehealth.hr.domain.exception.RequestConflictException;
import org.ehealth.hr.domain.exception.ValueNotFoundException;
import org.ehealth.hr.repository.EmployeeRepository;
import org.ehealth.hr.repository.SpecialistPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialistPaymentService implements ISpecialistPaymentService {

    private final SpecialistPaymentRepository specialistPaymentRepository;
    private final SurgeryClient surgeryClient;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<PaymentPerSurgeryDto> getPaymentPerSurgery() {

        List<SurgeryPaymentDto> surgeryPaymentDtos;
        try {
            surgeryPaymentDtos = this.surgeryClient.getSurgeryPayments();
        } catch (FeignException e) {
            throw new RequestConflictException("No se ha podido obtener las cirugias, intente mas tarde");
        }

        List<EmployeeDto> employeeDtos = this.employeeRepository.findAllBySpecialistTrueOrderByCreatedAtDesc(EmployeeDto.class);
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
            exist = this.surgeryClient.existSurge(surgeryPaymentDto.id());
        } catch (FeignException e) {
            throw new RequestConflictException("No se ha podido encontrar la cirugia que intenta pagar, intente mas tarde");
        }

        if (!exist){
            throw new ValueNotFoundException("La cirucia que intenta pagar no existe");
        }

        SpecialistPaymentEntity paymentEntity = SpecialistPaymentEntity
                .builder()
                .surgeryId(surgeryPaymentDto.id())
                .amount(surgeryPaymentDto.specialistFee())
                .specialistDoctor(employee)
                .build();

        this.specialistPaymentRepository.save(paymentEntity);
    }


}
