package org.ehealth.rx.service;

import org.ehealth.rx.domain.dto.CreateMedicineDto;
import org.ehealth.rx.domain.dto.MedicineDto;

import java.util.List;

public interface IMedicineService {

    void create(CreateMedicineDto createMedicineDto);
    List<MedicineDto> findAll();
    MedicineDto findById(Long id);
    void update(Long id, CreateMedicineDto createMedicineDto);
}
