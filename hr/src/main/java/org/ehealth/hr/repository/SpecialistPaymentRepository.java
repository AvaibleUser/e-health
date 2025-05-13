package org.ehealth.hr.repository;

import org.ehealth.hr.domain.dto.or.SpecialistPaymentDto;
import org.ehealth.hr.domain.dto.reports.PaymentEmployeeDto;
import org.ehealth.hr.domain.entity.SpecialistPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SpecialistPaymentRepository extends JpaRepository<SpecialistPaymentEntity, Long> {

    @Query("""
        SELECT new org.ehealth.hr.domain.dto.or.SpecialistPaymentDto(
            sp.id,
            sp.surgeryId,
            sp.specialistDoctor.id
        )
        FROM specialist_payment sp
    """)
    List<SpecialistPaymentDto> findAllSpecialistPayments();

    @Query("""
        SELECT new org.ehealth.hr.domain.dto.reports.PaymentEmployeeDto(
            e.id,
            e.fullName,
            e.cui,
            p.amount,
            p.paidAt
        )
        FROM specialist_payment p
        JOIN p.specialistDoctor e
        ORDER BY p.paidAt DESC
    """)
    List<PaymentEmployeeDto> findAllPaymentsProjected();

    @Query("""
    SELECT new org.ehealth.hr.domain.dto.reports.PaymentEmployeeDto(
        e.id,
        e.fullName,
        e.cui,
        p.amount,
        p.paidAt
    )
    FROM specialist_payment p
    JOIN p.specialistDoctor e
    WHERE p.paidAt BETWEEN :startDate AND :endDate
    ORDER BY p.paidAt DESC
    """)
    List<PaymentEmployeeDto> findAllPaymentsInRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

}

