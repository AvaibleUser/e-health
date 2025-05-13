package org.ehealth.ward.service.finance;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.finance.bill.BillDto;
import org.ehealth.ward.domain.dto.finance.bill.UpdateBillDto;
import org.ehealth.ward.domain.entity.finance.BillEntity;
import org.ehealth.ward.domain.entity.ward.PatientEntity;
import org.ehealth.ward.domain.exception.ValueNotFoundException;
import org.ehealth.ward.repository.finance.BillRepository;
import org.ehealth.ward.repository.ward.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillService implements IBillService {

    private final BillRepository billRepository;
    private final PatientRepository patientRepository;

    @Override
    public List<BillDto> findPatientBills(long patientId) {
        return billRepository.findByPatientId(patientId, BillDto.class);
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
            throw new ValueNotFoundException("La factura ya está cerrada");
        }

        bill.isClosed().ifPresent(dbBill::setClosed);
        bill.isPaid().ifPresent(dbBill::setPaid);

        billRepository.save(dbBill);
    }
}
