package org.ehealth.ward.service.finance;

import static java.util.Map.entry;
import static org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType.CONSULTATION;
import static org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType.HOSPITALIZED;
import static org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType.MEDICATION;
import static org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType.SURGERY;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.ehealth.ward.domain.dto.finance.billitem.AddBillItemDto;
import org.ehealth.ward.domain.dto.finance.billitem.BillItemDto;
import org.ehealth.ward.domain.dto.finance.report.BillItemReportDto;
import org.ehealth.ward.domain.dto.finance.report.ReportIncomeBill;
import org.ehealth.ward.domain.entity.finance.BillEntity;
import org.ehealth.ward.domain.entity.finance.BillItemEntity;
import org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType;
import org.ehealth.ward.domain.entity.or.SurgeryEntity;
import org.ehealth.ward.domain.entity.ward.AdmissionEntity;
import org.ehealth.ward.domain.entity.ward.PatientEntity;
import org.ehealth.ward.domain.exception.ValueNotFoundException;
import org.ehealth.ward.repository.finance.BillItemRepository;
import org.ehealth.ward.repository.finance.BillRepository;
import org.ehealth.ward.repository.or.SurgeryRepository;
import org.ehealth.ward.repository.ward.AdmissionRepository;
import org.ehealth.ward.repository.ward.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillItemService implements IBillItemService {

    private final BillItemRepository billItemRepository;
    private final BillRepository billRepository;
    private final PatientRepository patientRepository;
    private final AdmissionRepository admissionRepository;
    private final SurgeryRepository surgeryRepository;

    @Override
    public Page<BillItemDto> findPatientBillItems(long patientId, long billId, Pageable pageable, BillItemType type) {
        if (type == null) {
            return billItemRepository.findByBillPatientIdAndBillId(patientId, billId, pageable, BillItemDto.class);
        }
        return billItemRepository.findByBillPatientIdAndBillIdAndType(patientId, billId, type, pageable,
                BillItemDto.class);
    }

    @Override
    public Page<BillItemDto> findPatientBillItems(long patientId, Pageable pageable, BillItemType type) {
        if (type == null) {
            return billItemRepository.findByBillPatientId(patientId, pageable, BillItemDto.class);
        }
        return billItemRepository.findByBillPatientIdAndType(patientId, type, pageable, BillItemDto.class);
    }

    @Override
    @Transactional
    public void addConsultation(long patientId, AddBillItemDto billItem) {
        BillEntity bill = billRepository.findByPatientIdAndIsClosedFalse(patientId, BillEntity.class).orElse(null);

        if (bill != null) {
            addBillItem(patientId, bill.getId(), billItem);
            return;
        }

        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el paciente"));

        BillEntity newBill = BillEntity.builder()
                .patient(patient)
                .isClosed(true)
                .isPaid(true)
                .build();

        newBill.setBillItems(Set.of(BillItemEntity.builder()
                .concept(billItem.concept())
                .amount(billItem.amount())
                .bill(newBill)
                .build()));

        billRepository.save(newBill);
    }

    @Override
    @Transactional
    public void addBillItem(long patientId, long billId, AddBillItemDto billItem) {
        if (!patientRepository.existsById(patientId)) {
            throw new ValueNotFoundException("No se encontro el paciente");
        }
        BillEntity bill = billRepository.findByIdAndPatientId(billId, patientId, BillEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la factura"));

        if (bill.isClosed()) {
            throw new ValueNotFoundException("La factura ya está cerrada");
        }

        Entry<BillItemType, Long> related = billItem.admissionId().map(admissionId -> entry(HOSPITALIZED, admissionId))
                .or(() -> billItem.surgeryId().map(surgeryId -> entry(SURGERY, surgeryId)))
                .or(() -> billItem.saleId().map(saleId -> entry(MEDICATION, saleId)))
                .orElse(entry(CONSULTATION, null));

        BillItemEntity dbBillItem = BillItemEntity.builder()
                .concept(billItem.concept())
                .amount(billItem.amount())
                .type(related.getKey())
                .bill(bill)
                .build();

        switch (related.getKey()) {
            case HOSPITALIZED:
                dbBillItem.setAdmission(admissionRepository
                        .findByIdAndPatientId(patientId, related.getValue(), AdmissionEntity.class)
                        .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar la hospitalizacion")));
                break;

            case SURGERY:
                dbBillItem.setSurgery(surgeryRepository
                        .findByIdAndPatientId(patientId, related.getValue(), SurgeryEntity.class)
                        .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar la cirugía")));
                break;

            case MEDICATION:
                dbBillItem.setSaleId(billItem.saleId().get());
                break;

            default:
                break;
        }

        billItemRepository.save(dbBillItem);
    }

    @Override
    public ReportIncomeBill getReportIncomeBill(List<BillItemReportDto> items) {
        if (items == null || items.isEmpty()) {
            return ReportIncomeBill.builder()
                    .totalIncome(BigDecimal.ZERO)
                    .items(Collections.emptyList())
                    .build();
        }

        BigDecimal totalIncome = items.stream()
                .map(BillItemReportDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportIncomeBill.builder()
                .totalIncome(totalIncome)
                .items(items)
                .build();
    }

    @Override
    public ReportIncomeBill getReportIncomeBillInRange(LocalDate startDate, LocalDate endDate) {
        List<BillItemReportDto> items;

        if (startDate == null || endDate == null) {
            items = this.billItemRepository.findAllByOrderByCreatedAtDesc(BillItemReportDto.class);
            return this.getReportIncomeBill(items);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        items = billItemRepository.findAllByCreatedAtBetweenOrderByCreatedAtDesc(startInstant, endInstant,
                BillItemReportDto.class);

        return this.getReportIncomeBill(items);

    }

}
