package org.ehealth.rx.repository;

import org.ehealth.rx.domain.dto.report.MedicinePurchacheDto;
import org.ehealth.rx.domain.entity.PurchacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PurchachesRepository extends JpaRepository<PurchacheEntity, Long> {

    @Query("""
    SELECT new org.ehealth.rx.domain.dto.report.MedicinePurchacheDto(
        m.id,
        m.name,
        p.id,
        p.quantity,
        p.unitCost,
        p.purchasedAt
    )
    FROM purchase p
    JOIN p.medicine m
    ORDER BY p.purchasedAt DESC
    """)
    List<MedicinePurchacheDto> findAllPurchasesWithMedicine();

    @Query("""
    SELECT new org.ehealth.rx.domain.dto.report.MedicinePurchacheDto(
        m.id,
        m.name,
        p.id,
        p.quantity,
        p.unitCost,
        p.purchasedAt
    )
    FROM purchase p
    JOIN p.medicine m
    WHERE p.purchasedAt BETWEEN :startDate AND :endDate
    ORDER BY p.purchasedAt DESC
    """)
    List<MedicinePurchacheDto> findAllPurchasesWithMedicineInRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);




}
