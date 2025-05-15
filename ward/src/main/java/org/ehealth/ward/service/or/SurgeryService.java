package org.ehealth.ward.service.or;

import static org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType.SURGERY;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.or.surgery.AddSurgeryDto;
import org.ehealth.ward.domain.dto.or.surgery.SurgeryDto;
import org.ehealth.ward.domain.dto.or.surgery.SurgeryPaymentDto;
import org.ehealth.ward.domain.dto.or.surgery.UpdateSurgeryDto;
import org.ehealth.ward.domain.entity.finance.BillEntity;
import org.ehealth.ward.domain.entity.finance.BillItemEntity;
import org.ehealth.ward.domain.entity.finance.TariffEntity;
import org.ehealth.ward.domain.entity.or.SurgeryEntity;
import org.ehealth.ward.domain.entity.ward.PatientEntity;
import org.ehealth.ward.domain.exception.ValueNotFoundException;
import org.ehealth.ward.repository.finance.BillItemRepository;
import org.ehealth.ward.repository.finance.BillRepository;
import org.ehealth.ward.repository.finance.TariffRepository;
import org.ehealth.ward.repository.or.SurgeryRepository;
import org.ehealth.ward.repository.ward.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurgeryService implements ISurgeryService {

    private final SurgeryRepository surgeryRepository;
    private final TariffRepository tariffRepository;
    private final PatientRepository patientRepository;
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;

    @Override
    public Optional<SurgeryDto> findByIdAndPatientId(long id, long patientId) {
        return this.surgeryRepository.findByIdAndPatientId(id, patientId, SurgeryDto.class);
    }

    @Override
    public Page<SurgeryDto> findAllByPatientId(long patientId, Pageable pageable) {
        return this.surgeryRepository.findAllByPatientId(patientId, pageable, SurgeryDto.class);
    }

    @Override
    public List<SurgeryPaymentDto> getSurgeryPaymentDto() {
        return this.surgeryRepository.findAllSurgeryPaymentsBySpecialistType();
    }

    @Override
    public boolean existSurgeryPayment(Long specialistId) {
        return this.surgeryRepository.existsById(specialistId);
    }

    @Override
    public void addSurgery(long patientId, AddSurgeryDto surgery) {
        PatientEntity patient = patientRepository.findById(patientId).orElseThrow(
                () -> new ValueNotFoundException("No se encontro el paciente con id " + patientId));

        TariffEntity tariff = tariffRepository.findById(surgery.tariffId())
                .orElseThrow(() -> new ValueNotFoundException(
                        "No se encontro el tipo de paciente con id " + surgery.tariffId()));

        SurgeryEntity dbSurgery = SurgeryEntity.builder()
                .performedDate(surgery.performedDate())
                .description(surgery.description())
                .patient(patient)
                .tariff(tariff)
                .build();

        BillEntity bill = billRepository.findByPatientIdAndIsClosedFalse(patientId, BillEntity.class)
                .orElseGet(() -> {
                    BillEntity dbBill = BillEntity.builder()
                            .patient(patient)
                            .isClosed(true)
                            .isPaid(true)
                            .build();

                    billRepository.save(dbBill);
                    return dbBill;
                });

        BillItemEntity item = BillItemEntity.builder()
                .concept("Cirugía de " + surgery.description() + " el " + surgery.performedDate())
                .amount(BigDecimal.ONE)
                .type(SURGERY)
                .bill(bill)
                .surgery(dbSurgery)
                .build();

        if (bill.getTotal() == null) {
            bill.setTotal(tariff.getPrice());
        } else {
            bill.setTotal(bill.getTotal().add(tariff.getPrice()));
        }

        billItemRepository.save(item);
        billRepository.save(bill);
    }

    @Override
    public void updateSurgery(long surgeryId, long patientId, UpdateSurgeryDto surgery) {
        SurgeryEntity surgeryEntity = surgeryRepository.findByIdAndPatientId(surgeryId, patientId, SurgeryEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la cirugía con id " + surgeryId));

        surgery.performedDate().ifPresent(performedDate -> surgeryEntity.setPerformedDate(performedDate));
        surgery.description().ifPresent(description -> surgeryEntity.setDescription(description));

        surgeryRepository.save(surgeryEntity);
    }
}
