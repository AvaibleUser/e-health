package org.ehealth.hr.repository;

import org.ehealth.hr.domain.dto.ContractDto;
import org.ehealth.hr.domain.entity.ContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, Long> {

    <T> Optional<T> findFirstByEmployeeIdOrderByCreatedAtDesc(Long employeeId, Class<T> type);
    <T> List<T> findAllByEmployeeIdOrderByCreatedAtDesc(Long employeeId,Class<T> type);

    <T> List<T> findAllByOrderByCreatedAtDesc(Class<T> type);

    @Query("""
        SELECT new org.ehealth.hr.domain.dto.ContractDto(
        c.id,
        c.employee.id,
        c.salary,
        c.igssDiscount,
        c.irtraDiscount,
        c.terminationReason,
        c.terminationDescription,
        c.startDate,
        c.endDate,
        c.createdAt,
        c.updatedAt
        ) FROM contract c
        WHERE c.startDate BETWEEN :startDate AND :endDate
        ORDER BY c.createdAt DESC
    """)
    List<ContractDto> findAllContractsBetweenDates(@Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);
}
