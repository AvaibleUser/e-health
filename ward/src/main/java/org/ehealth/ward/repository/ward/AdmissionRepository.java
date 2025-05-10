package org.ehealth.ward.repository.ward;

import java.util.Optional;

import org.ehealth.ward.domain.dto.ward.admission.AdmissionDto;
import org.ehealth.ward.domain.entity.ward.AdmissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmissionRepository extends JpaRepository<AdmissionEntity, Long> {

    <T> Optional<T> findById(Long id, Class<T> type);

    <T> Optional<T> findByIdAndPatientId(Long id, Long patientId, Class<T> type);

    <T> Page<T> findAllPageableBy(Pageable pageable, Class<T> type);

    <T> Page<T> findAllByPatientId(Long patientId, Pageable pageable, Class<T> type);

    @Query("""
            SELECT new org.ehealth.ward.domain.dto.ward.admission.AdmissionDto(
                a.id,
                a.admissionDate,
                a.dischargeDate,
                a.status,
                a.room.id,
                a.room.number,
                a.room.costPerDay,
                a.createdAt,
                a.updatedAt
            )
            FROM admission a
            WHERE a.patient.id = :patientId
                AND (a.dischargeDate IS NULL OR a.dischargeDate > CURRENT_DATE)
            """)
    Optional<AdmissionDto> findByPatientIdAndIsAdmitted(Long patientId);
}
