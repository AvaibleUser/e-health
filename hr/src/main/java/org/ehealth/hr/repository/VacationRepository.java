package org.ehealth.hr.repository;

import org.ehealth.hr.domain.entity.VacationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationRepository extends JpaRepository<VacationEntity, Long> {
}
