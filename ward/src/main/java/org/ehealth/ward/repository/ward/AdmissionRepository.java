package org.ehealth.ward.repository.ward;

import java.util.Optional;

import org.ehealth.ward.domain.entity.ward.AdmissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmissionRepository extends JpaRepository<AdmissionEntity, Long> {

    <T> Optional<T> findById(Long id, Class<T> type);

    <T> Optional<T> findByIdAndPatientId(Long id, Long patientId, Class<T> type);

    <T> Page<T> findAllPageableBy(Pageable pageable, Class<T> type);

    <T> Page<T> findAllByPatientId(Long patientId, Pageable pageable, Class<T> type);

    boolean existsByPatientIdAndStatusIsAdmitted(Long patientId);
}
