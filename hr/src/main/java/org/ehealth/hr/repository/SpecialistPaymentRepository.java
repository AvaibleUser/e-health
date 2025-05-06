package org.ehealth.hr.repository;

import org.ehealth.hr.domain.dto.or.SpecialistPaymentDto;
import org.ehealth.hr.domain.entity.SpecialistPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}

