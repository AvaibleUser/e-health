package org.ehealth.rx.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.ehealth.rx.client.EmployeeClient;
import org.ehealth.rx.client.PatientClient;
import org.ehealth.rx.domain.dto.CreateSaleDto;
import org.ehealth.rx.domain.dto.ItemSaleDto;
import org.ehealth.rx.domain.dto.employee.EmployeeDto;
import org.ehealth.rx.domain.entity.MedicineEntity;
import org.ehealth.rx.domain.entity.SaleEntity;
import org.ehealth.rx.domain.exception.BadRequestException;
import org.ehealth.rx.domain.exception.RequestConflictException;
import org.ehealth.rx.domain.exception.ValueNotFoundException;
import org.ehealth.rx.repository.MedicineRepository;
import org.ehealth.rx.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService implements ISaleService {

    private final SaleRepository saleRepository;
    private final PatientClient patientClient;
    private final EmployeeClient employeeClient;
    private final MedicineRepository medicineRepository;

    @Override
    public EmployeeDto validateEntities(String cui, Long patientId) {
        EmployeeDto employee;
        try {
            boolean existPatient = patientClient.existSurge(patientId);
            employee = employeeClient.findEmployeeByCui(cui);

            if (!existPatient) {
                throw new ValueNotFoundException("El paciente no existe");
            }
            if (employee==null) {
                throw new ValueNotFoundException("El empleado no existe");
            }
            return employee;
        } catch (FeignException e) {
            throw new RequestConflictException("No se pudo validar al paciente o empleado, intente más tarde");
        }
    }

    @Override
    public void validateItemList(List<ItemSaleDto> items) {
        if (items == null || items.isEmpty()) {
            throw new BadRequestException("Debe agregar al menos un producto para realizar la venta");
        }
    }

    @Override
    public Set<Long> extractMedicineIds(List<ItemSaleDto> items) {
        return items.stream()
                .map(ItemSaleDto::medicineId)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Long, MedicineEntity> loadMedicinesById(Set<Long> medicineIds) {
        List<MedicineEntity> medicines = medicineRepository.findAllById(medicineIds);
        return medicines.stream()
                .collect(Collectors.toMap(MedicineEntity::getId, Function.identity()));
    }

    @Override
    public void validateStockAvailability(Map<Long, MedicineEntity> medicineMap, List<ItemSaleDto> items) {
        for (ItemSaleDto item : items) {
            MedicineEntity medicine = medicineMap.get(item.medicineId());
            if (medicine == null) {
                throw new RequestConflictException("El medicamento con ID " + item.medicineId() + " no existe");
            }

            int newStock = medicine.getStock() - item.quantity();
            if (newStock < 0) {
                throw new RequestConflictException("El stock de " + medicine.getName() + " es insuficiente");
            }

            medicine.setStock(newStock);
        }
    }

    @Override
    public List<SaleEntity> buildSales(Long employeeId, CreateSaleDto dto, Map<Long, MedicineEntity> medicineMap) {
        return dto.items().stream()
                .map(item -> {
                    MedicineEntity medicine = medicineMap.get(item.medicineId());
                    return SaleEntity.builder()
                            .employeeId(employeeId)
                            .patientId(dto.patientId())
                            .medicine(medicine)
                            .quantity(item.quantity())
                            .unitPrice(medicine.getUnitPrice())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createSaleTotal(String cui, CreateSaleDto createSaleDto) {
        EmployeeDto employee = validateEntities(cui, createSaleDto.patientId());
        validateItemList(createSaleDto.items());

        Set<Long> medicineIds = extractMedicineIds(createSaleDto.items());
        Map<Long, MedicineEntity> medicineMap = loadMedicinesById(medicineIds);
        validateStockAvailability(medicineMap, createSaleDto.items());

        List<SaleEntity> sales = buildSales(employee.id(), createSaleDto, medicineMap);
        saleRepository.saveAll(sales);

        // Guardar el nuevo stock actualizado
        medicineRepository.saveAll(medicineMap.values());
    }


}
