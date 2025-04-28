package org.ehealth.gatekeeper.repository;

import org.ehealth.gatekeeper.domain.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    boolean existsByCui(String cui);
}
