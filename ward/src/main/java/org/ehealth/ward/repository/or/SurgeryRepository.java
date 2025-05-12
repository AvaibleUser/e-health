package org.ehealth.ward.repository.or;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.or.surgery.SurgeryPaymentDto;
import org.ehealth.ward.domain.entity.or.SurgeryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SurgeryRepository extends JpaRepository<SurgeryEntity, Long> {

    <T> Optional<T> findById(Long id, Class<T> type);

    <T> Optional<T> findByIdAndPatientId(Long id, Long patientId, Class<T> type);

    <T> Page<T> findAllByPatientId(Long patientId, Pageable pageable, Class<T> type);

    @Query("""
            SELECT new org.ehealth.ward.domain.dto.or.surgery.SurgeryPaymentDto(
                t.specialistFee,
                s.id,
                s.description,
                s.performedDate,
                ss.employeeId
            )
            FROM surgery_specialist ss
            JOIN ss.surgery s
            JOIN s.tariff t
            WHERE ss.type = 'SPECIALIST'
            """)
    List<SurgeryPaymentDto> findAllSurgeryPaymentsBySpecialistType();

}
