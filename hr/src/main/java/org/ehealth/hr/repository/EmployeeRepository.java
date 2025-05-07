package org.ehealth.hr.repository;

import java.util.List;
import java.util.Optional;

import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByCui(String cui);

    <T> Optional<T> findByCui(String cui, Class<T> type);

    <T> List<T> findAllByOrderByCreatedAtDesc(Class<T> type);

    <T> List<T> findAllByAreaIdOrderByCreatedAtDesc(Long areaId, Class<T> type);

}
