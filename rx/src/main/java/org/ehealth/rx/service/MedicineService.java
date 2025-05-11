package org.ehealth.rx.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.rx.domain.dto.CreateMedicineDto;
import org.ehealth.rx.domain.dto.MedicineDto;
import org.ehealth.rx.domain.entity.MedicineEntity;
import org.ehealth.rx.domain.exception.BadRequestException;
import org.ehealth.rx.domain.exception.RequestConflictException;
import org.ehealth.rx.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineService implements IMedicineService {

    private final MedicineRepository medicineRepository;

    @Transactional
    @Override
    public void create(CreateMedicineDto createMedicineDto) {
        boolean existsByName = medicineRepository.existsByNameIgnoreCase(createMedicineDto.name());
        if (existsByName) {
            throw new RequestConflictException("Ya existe un medicamento registrado con el nombre: " + createMedicineDto.name());
        }

        MedicineEntity medicine = MedicineEntity.builder()
                .name(createMedicineDto.name())
                .unitPrice(createMedicineDto.unitPrice())
                .unitCost(createMedicineDto.unitCost())
                .stock(createMedicineDto.stock())
                .minStock(createMedicineDto.minStock())
                .build();

        medicineRepository.save(medicine);
    }

    @Override
    public List<MedicineDto> findAll() {
        return this.medicineRepository.findAllByOrderByCreatedAtDesc(MedicineDto.class);
    }

    @Override
    public MedicineDto findById(Long id) {
        MedicineEntity medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("La medicina no existe"));

        return MedicineDto
                .builder()
                .id(medicine.getId())
                .name(medicine.getName())
                .unitPrice(medicine.getUnitPrice())
                .unitCost(medicine.getUnitCost())
                .stock(medicine.getStock())
                .minStock(medicine.getMinStock())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }

    @Transactional
    public void update(Long id, CreateMedicineDto createMedicineDto) {
        MedicineEntity medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("La medicina no existe"));

        medicine.setName(createMedicineDto.name());
        medicine.setUnitPrice(createMedicineDto.unitPrice());
        medicine.setUnitCost(createMedicineDto.unitCost());
        medicine.setStock(createMedicineDto.stock());
        medicine.setMinStock(createMedicineDto.minStock());
        medicineRepository.save(medicine);
    }

}
