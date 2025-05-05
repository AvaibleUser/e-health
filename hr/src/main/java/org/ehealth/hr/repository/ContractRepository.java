package org.ehealth.hr.repository;

import org.ehealth.hr.domain.entity.ContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, Long> {

    <T> Optional<T> findFirstByEmployeeIdOrderByCreatedAtDesc(Long employeeId, Class<T> type);
    <T> List<T> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId,Class<T> type);

}
