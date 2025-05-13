package org.ehealth.rx.repository;

import org.ehealth.rx.domain.entity.MedicineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<MedicineEntity, Long> {

    boolean existsByNameIgnoreCase(String name);

    <T> List<T> findAllByOrderByCreatedAtDesc(Class<T> type);

}
