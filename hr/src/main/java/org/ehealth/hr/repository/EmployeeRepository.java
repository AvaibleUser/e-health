package org.ehealth.hr.repository;

import java.util.Optional;

import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    <T> Optional<T> findByCui(String cui, Class<T> type);
}
