package org.ehealth.rx.repository;

import org.ehealth.rx.domain.dto.report.SaleMedicineDto;
import org.ehealth.rx.domain.entity.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity, Long> {

    @Query("""
        SELECT new org.ehealth.rx.domain.dto.report.SaleMedicineDto(
            m.id,
            m.name,
            m.unitCost,
            s.id,
            s.quantity,
            s.unitPrice,
            s.soldAt
        )
        FROM sale s
        JOIN s.medicine m
        ORDER BY s.soldAt DESC
    """)
    List<SaleMedicineDto> findAllSalesWithMedicine();

    @Query("""
    SELECT new org.ehealth.rx.domain.dto.report.SaleMedicineDto(
        m.id,
        m.name,
        m.unitCost,
        s.id,
        s.quantity,
        s.unitPrice,
        s.soldAt
    )
    FROM sale s
    JOIN s.medicine m
    WHERE s.soldAt BETWEEN :start AND :end
    ORDER BY s.soldAt DESC
    """)
    List<SaleMedicineDto> findSalesWithMedicineBetweenDates(
            @Param("start") Instant start,
            @Param("end") Instant end
    );



}
