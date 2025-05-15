package org.ehealth.ward.service.finance;

import static org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType.HOSPITALIZED;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.ehealth.ward.domain.dto.finance.bill.BillDto;
import org.ehealth.ward.domain.dto.finance.bill.BillDto.NativeBillDto;
import org.ehealth.ward.domain.dto.finance.bill.UpdateBillDto;
import org.ehealth.ward.domain.entity.finance.BillEntity;
import org.ehealth.ward.domain.entity.finance.BillItemEntity;
import org.ehealth.ward.domain.entity.ward.AdmissionEntity;
import org.ehealth.ward.domain.entity.ward.PatientEntity;
import org.ehealth.ward.domain.exception.RequestConflictException;
import org.ehealth.ward.domain.exception.ValueNotFoundException;
import org.ehealth.ward.repository.finance.BillRepository;
import org.ehealth.ward.repository.ward.PatientRepository;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillService implements IBillService {

    private final BillRepository billRepository;
    private final PatientRepository patientRepository;

    @Override
    public Page<BillDto> findPatientBills(long patientId, Pageable pageable) {
        return billRepository.findAllByPatientId(patientId, pageable, BillDto.class);
    }

    @Override
    public List<BillDto> findOpenPatientBills(long patientId) {
        return billRepository.findAllByPatientIdAndIsClosedFalse(patientId)
                .stream()
                .map(BillDto::new)
                .toList();
    }

    @Override
    public Optional<BillDto> findPatientBillById(long patientId, long billId) {
        return billRepository.findByIdAndPatientId(billId, patientId, BillDto.class);
    }

    @Override
    @Transactional
    public void addBill(long patientId) {
        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el paciente"));

        if (billRepository.existsByPatientIdAndIsClosedFalse(patientId)) {
            throw new ValueNotFoundException("No se puede abrir una nueva factura porque ya hay una abierta");
        }

        BillEntity bill = BillEntity.builder()
                .patient(patient)
                .build();

        billRepository.save(bill);
    }

    @Override
    @Transactional
    public void updateBill(long billId, long patientId, UpdateBillDto bill) {
        if (!patientRepository.existsById(patientId)) {
            throw new ValueNotFoundException("No se encontro el paciente");
        }
        BillEntity dbBill = billRepository.findByIdAndPatientId(billId, patientId, BillEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la factura"));

        if (dbBill.isClosed()) {
            throw new RequestConflictException("La factura ya está cerrada");
        }

        if (bill.isClosed().orElse(false)) {
            Hibernate.initialize(dbBill.getBillItems());
            if (dbBill.getBillItems().isEmpty()) {
                billRepository.delete(dbBill);
                return;
            }
            if (dbBill.getBillItems()
                    .stream()
                    .filter(b -> {
                        if (b.getType() == HOSPITALIZED) {
                            Hibernate.initialize(b.getAdmission());
                            return true;
                        }
                        return false;
                    })
                    .map(BillItemEntity::getAdmission)
                    .filter(Objects::nonNull)
                    .map(AdmissionEntity::getDischargeDate)
                    .anyMatch(d -> d == null || d.isAfter(LocalDate.now()))) {
                throw new RequestConflictException(
                        "La factura no puede ser cerrada porque tiene hospitalizaciones pendientes");
            }
            billRepository.findAllByPatientIdAndIsClosedFalse(patientId)
                    .stream()
                    .findAny()
                    .map(NativeBillDto::total)
                    .map(Number::doubleValue)
                    .map(BigDecimal::valueOf)
                    .ifPresent(dbBill::setTotal);
        }

        bill.isClosed().ifPresent(dbBill::setClosed);
        bill.isPaid().ifPresent(dbBill::setPaid);

        billRepository.save(dbBill);
    }
}
