package org.ehealth.ward.repository.finance;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.entity.finance.BillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<BillEntity, Long> {

    <T> List<T> findByPatientId(long patientId, Class<T> type);

    <T> Optional<T> findByIdAndPatientId(long id, long patientId, Class<T> type);

    <T> Optional<T> findByPatientIdAndIsClosedFalse(long patientId, Class<T> type);

    boolean existsByPatientIdAndIsClosedFalse(long patientId);
}
