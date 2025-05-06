package org.ehealth.hr.repository;

import org.ehealth.hr.domain.entity.AreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends JpaRepository<AreaEntity, Long> {
    boolean existsByNameIgnoreCase(String name);
}
