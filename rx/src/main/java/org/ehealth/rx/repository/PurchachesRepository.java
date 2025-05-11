package org.ehealth.rx.repository;

import org.ehealth.rx.domain.entity.PurchacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchachesRepository extends JpaRepository<PurchacheEntity, Long> {
}
