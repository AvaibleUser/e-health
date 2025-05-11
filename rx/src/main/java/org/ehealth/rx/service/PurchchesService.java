package org.ehealth.rx.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.rx.domain.dto.CreatePurchacheDto;
import org.ehealth.rx.domain.entity.MedicineEntity;
import org.ehealth.rx.domain.entity.PurchacheEntity;
import org.ehealth.rx.domain.exception.BadRequestException;
import org.ehealth.rx.repository.MedicineRepository;
import org.ehealth.rx.repository.PurchachesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
