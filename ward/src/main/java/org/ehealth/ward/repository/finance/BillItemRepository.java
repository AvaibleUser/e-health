package org.ehealth.ward.repository.finance;

import java.time.Instant;
import java.util.List;

import org.ehealth.ward.domain.entity.finance.BillItemEntity;
import org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillItemRepository extends JpaRepository<BillItemEntity, Long> {

    <T> Page<T> findByBillPatientId(long patientId, Pageable pageable, Class<T> type);

    <T> Page<T> findByBillPatientIdAndType(long patientId, BillItemType type, Pageable pageable, Class<T> dtoType);

    <T> Page<T> findByBillPatientIdAndBillId(long patientId, long billId, Pageable pageable, Class<T> type);

    <T> Page<T> findByBillPatientIdAndBillIdAndType(long patientId, long billId, BillItemType type, Pageable pageable,
            Class<T> dtoType);

    <T> List<T> findAllByOrderByCreatedAtDesc(Class<T> type);

    <T> List<T> findAllByCreatedAtBetweenOrderByCreatedAtDesc(Instant start, Instant end, Class<T> type);
}
