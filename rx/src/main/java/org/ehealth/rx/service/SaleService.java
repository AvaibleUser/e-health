package org.ehealth.rx.service;

import feign.FeignException;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.ehealth.rx.client.EmployeeClient;
import org.ehealth.rx.client.PatientClient;
import org.ehealth.rx.domain.dto.CreateSaleDto;
import org.ehealth.rx.domain.dto.ItemSaleDto;
import org.ehealth.rx.domain.dto.employee.EmployeeDto;
import org.ehealth.rx.domain.dto.report.*;
import org.ehealth.rx.domain.entity.MedicineEntity;
import org.ehealth.rx.domain.entity.SaleEntity;
import org.ehealth.rx.domain.exception.BadRequestException;
import org.ehealth.rx.domain.exception.RequestConflictException;
import org.ehealth.rx.domain.exception.ValueNotFoundException;
import org.ehealth.rx.repository.MedicineRepository;
import org.ehealth.rx.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
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

    @Override
    public List<ReportSaleMedicineDto> getReportSalesMedicinePerMedicine(List<SaleMedicineDto> saleMedicineDtos) {

        // Agrupamos por medicamento
        Map<Long, List<SaleMedicineDto>> groupedByMedicine = saleMedicineDtos.stream()
                .collect(Collectors.groupingBy(SaleMedicineDto::medicineId));

        List<ReportSaleMedicineDto> reports = new ArrayList<>();

        for (Map.Entry<Long, List<SaleMedicineDto>> entry : groupedByMedicine.entrySet()) {
            List<SaleMedicineDto> sales = entry.getValue();
            SaleMedicineDto anySale = sales.get(0);

            List<ItemsSaleMedicineDto> items = new ArrayList<>();
            int totalSold = 0;
            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalCost = BigDecimal.ZERO;

            for (SaleMedicineDto sale : sales) {
                int quantity = sale.quantity();
                BigDecimal unitPrice = sale.unitPrice();
                BigDecimal unitCost = sale.unitCost();

                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
                BigDecimal subCost = unitCost.multiply(BigDecimal.valueOf(quantity));
                BigDecimal profit = subtotal.subtract(subCost);

                totalSold += quantity;
                totalIncome = totalIncome.add(subtotal);
                totalCost = totalCost.add(subCost);

                items.add(ItemsSaleMedicineDto.builder()
                        .saleId(sale.saleId())
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .soldAt(sale.soldAt())
                        .unitCost(unitCost)
                        .Subtotal(subtotal)
                        .SubCost(subCost)
                        .Profit(profit)
                        .build());
            }

            BigDecimal totalProfit = totalIncome.subtract(totalCost);

            reports.add(ReportSaleMedicineDto.builder()
                    .medicineId(anySale.medicineId())
                    .name(anySale.name())
                    .totalSold(totalSold)
                    .totalIncome(totalIncome)
                    .totalProfit(totalProfit)
                    .items(items)
                    .build());
        }

        return reports;
    }

    @Override
    public List<ReportSaleMedicineDto> getReportSalesMedicinePerMedicineInRange(LocalDate startDate, LocalDate endDate) {
        List<SaleMedicineDto> saleMedicineDtos;

        if (startDate == null || endDate == null) {
            saleMedicineDtos = this.saleRepository.findAllSalesWithMedicine();
            return this.getReportSalesMedicinePerMedicine(saleMedicineDtos);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        saleMedicineDtos = this.saleRepository.findSalesWithMedicineBetweenDates(startInstant, endInstant);

        return this.getReportSalesMedicinePerMedicine(saleMedicineDtos);
    }

    @Override
    public List<ReportSalesPerEmployeeDto> getReportSalesMedicinePerEmployee(List<SaleMedicineDto> saleMedicineDtos) {

        // Obtener empleados
        List<EmployeeDto> employees;
        try {
            employees = this.employeeClient.findAllEmployees();
        } catch (FeignException e) {
            throw new RequestConflictException("No se obtener empleados para el reporte");
        }

        // Agrupar las ventas por empleado
        Map<Long, List<SaleMedicineDto>> groupedByEmployee = saleMedicineDtos.stream()
                .collect(Collectors.groupingBy(SaleMedicineDto::employeeId));

        List<ReportSalesPerEmployeeDto> reportList = new ArrayList<>();

        for (EmployeeDto employee : employees) {
            Long empId = employee.id();
            List<SaleMedicineDto> employeeSales = groupedByEmployee.get(empId);
            if (employeeSales == null || employeeSales.isEmpty()) {
                continue;
            }

            List<ItemsSalePerEmployeeDto> items = new ArrayList<>();
            int totalSold = 0;
            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalCost = BigDecimal.ZERO;

            for (SaleMedicineDto sale : employeeSales) {
                int quantity = sale.quantity();
                BigDecimal unitPrice = sale.unitPrice();
                BigDecimal unitCost = sale.unitCost();

                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
                BigDecimal subCost = unitCost.multiply(BigDecimal.valueOf(quantity));
                BigDecimal profit = subtotal.subtract(subCost);

                totalSold += quantity;
                totalIncome = totalIncome.add(subtotal);
                totalCost = totalCost.add(subCost);

                items.add(ItemsSalePerEmployeeDto.builder()
                        .saleId(sale.saleId())
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .soldAt(sale.soldAt())
                        .unitCost(unitCost)
                        .name(sale.name())
                        .Subtotal(subtotal)
                        .SubCost(subCost)
                        .Profit(profit)
                        .build());
            }

            BigDecimal totalProfit = totalIncome.subtract(totalCost);

            reportList.add(ReportSalesPerEmployeeDto.builder()
                    .employeeId(empId)
                    .employeeName(employee.fullName())
                    .cui(employee.cui())
                    .totalSold(totalSold)
                    .totalIncome(totalIncome)
                    .totalProfit(totalProfit)
                    .items(items)
                    .build());
        }

        return reportList;
    }

    @Override
    public List<ReportSalesPerEmployeeDto> getReportSalesMedicineEmployeeInRange(LocalDate startDate, LocalDate endDate){
        List<SaleMedicineDto> saleMedicineDtos;

        if (startDate == null || endDate == null) {
            saleMedicineDtos = this.saleRepository.findAllSalesWithMedicine();
            return this.getReportSalesMedicinePerEmployee(saleMedicineDtos);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        saleMedicineDtos = this.saleRepository.findSalesWithMedicineBetweenDates(startInstant, endInstant);

        return this.getReportSalesMedicinePerEmployee(saleMedicineDtos);

    }

    @Override
    public ReportSalesTotal getReportSalesTotal(List<SaleMedicineDto> saleMedicineDtos) {
        if (saleMedicineDtos == null || saleMedicineDtos.isEmpty()) {
            return ReportSalesTotal.builder()
                    .totalIncome(BigDecimal.ZERO)
                    .items(Collections.emptyList())
                    .build();
        }

        BigDecimal totalIncome = saleMedicineDtos.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportSalesTotal.builder()
                .totalIncome(totalIncome)
                .items(saleMedicineDtos)
                .build();
    }

    @Override
    @Generated
    public ReportSalesTotal getReportSalesTotalInRange(LocalDate startDate, LocalDate endDate){
        List<SaleMedicineDto> saleMedicineDtos;

        if (startDate == null || endDate == null) {
            saleMedicineDtos = this.saleRepository.findAllSalesWithMedicine();
            return this.getReportSalesTotal(saleMedicineDtos);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        saleMedicineDtos = this.saleRepository.findSalesWithMedicineBetweenDates(startInstant, endInstant);

        return this.getReportSalesTotal(saleMedicineDtos);
    }

}
