package org.ehealth.ward.repository;

import org.ehealth.ward.domain.dto.or.SurgeryPaymentDto;
import org.ehealth.ward.domain.entity.or.SurgeryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurgeryRepository extends JpaRepository<SurgeryEntity, Long> {

    @Query("""
    SELECT new org.ehealth.ward.domain.dto.or.SurgeryPaymentDto(
        t.specialistFee,
        s.id,
        s.description,
        s.performedDate,
        ss.employeeId
    )
    FROM surgery_specialist ss
    JOIN ss.surgery s
    JOIN s.tariff t
    WHERE ss.type = org.ehealth.ward.domain.entity.or.SurgerySpecialist.SurgerySpecialistType.SPECIALIST
    """)
    List<SurgeryPaymentDto> findAllSurgeryPaymentsBySpecialistType();

}
