package org.ehealth.rx.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.rx.domain.dto.CreatePurchacheDto;
import org.ehealth.rx.domain.dto.report.MedicinePurchacheDto;
import org.ehealth.rx.domain.dto.report.ReportExpenseMedicinePurchacheDto;
import org.ehealth.rx.domain.entity.MedicineEntity;
import org.ehealth.rx.domain.entity.PurchacheEntity;
import org.ehealth.rx.domain.exception.BadRequestException;
import org.ehealth.rx.repository.MedicineRepository;
import org.ehealth.rx.repository.PurchachesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchchesService implements IPurchchesService{

    private final PurchachesRepository purchachesRepository;
    private final MedicineRepository medicineRepository;


    @Override
    @Transactional
    public void create(Long medicineId, CreatePurchacheDto createPurchacheDto) {
        MedicineEntity medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new BadRequestException("La medicina no existe"));

        int totalQuantity =  createPurchacheDto.quantity() + medicine.getStock();

        PurchacheEntity purchache = PurchacheEntity
                .builder()
                .medicine(medicine)
                .quantity(createPurchacheDto.quantity())
                .unitCost(medicine.getUnitCost())
                .build();

        medicine.setStock(totalQuantity);

        this.medicineRepository.save(medicine);

        purchachesRepository.save(purchache);
    }

    @Override
    public ReportExpenseMedicinePurchacheDto getReportExpensePurchasesMedicine(List<MedicinePurchacheDto> items) {
        if (items == null || items.isEmpty()) {
            return ReportExpenseMedicinePurchacheDto.builder()
                    .amountExpense(BigDecimal.ZERO)
                    .items(Collections.emptyList())
                    .build();
        }

        BigDecimal total = items.stream()
                .map(item -> item.unitCost().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportExpenseMedicinePurchacheDto.builder()
                .amountExpense(total)
                .items(items)
                .build();
    }

    @Override
    public ReportExpenseMedicinePurchacheDto getReportExpensePurchasesMedicineInRange(LocalDate startDate, LocalDate endDate) {
        List<MedicinePurchacheDto> items;

        if (startDate == null || endDate == null) {
            items = this.purchachesRepository.findAllPurchasesWithMedicine();
            return this.getReportExpensePurchasesMedicine(items);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        items = purchachesRepository.findAllPurchasesWithMedicineInRange(startInstant,endInstant);

        return this.getReportExpensePurchasesMedicine(items);

    }

}
