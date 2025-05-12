package org.ehealth.ward.repository.finance;

import org.ehealth.ward.domain.entity.finance.BillItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItemEntity, Long> {
    <T> List<T> findAllByOrderByCreatedAtDesc(Class<T> type);
    <T> List<T> findAllByCreatedAtBetweenOrderByCreatedAtDesc(Instant start, Instant end, Class<T> type);

}
