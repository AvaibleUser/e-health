package org.ehealth.hr.repository;

import org.ehealth.hr.domain.dto.vacation.VacationPendingDto;
import org.ehealth.hr.domain.entity.VacationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<VacationEntity, Long> {

    @Query("""
SELECT new org.ehealth.hr.domain.dto.vacation.VacationPendingDto(
    e.id,
    e.fullName,
    e.cui,
    a.name,
    v.id,
    v.requestedDate,
    v.startDate,
    v.endDate,
    v.approved,
    v.state
)
FROM vacation v
JOIN v.employee e
JOIN e.area a
WHERE v.state = org.ehealth.hr.domain.entity.VacationEntity.State.PENDIENTE
  AND v.id = (
      SELECT MAX(v2.id)
      FROM vacation v2
      WHERE v2.employee.id = v.employee.id
        AND v2.state = org.ehealth.hr.domain.entity.VacationEntity.State.PENDIENTE
  )
ORDER BY v.id DESC
""")
    List<VacationPendingDto> findAllPendingVacations();


    @Query("""
SELECT new org.ehealth.hr.domain.dto.vacation.VacationPendingDto(
    e.id,
    e.fullName,
    e.cui,
    a.name,
    v.id,
    v.requestedDate,
    v.startDate,
    v.endDate,
    v.approved,
    v.state
)
FROM vacation v
JOIN v.employee e
JOIN e.area a
WHERE v.state = org.ehealth.hr.domain.entity.VacationEntity.State.APROVADA
  AND v.id = (
      SELECT MAX(v2.id)
      FROM vacation v2
      WHERE v2.employee.id = v.employee.id
        AND v2.state = org.ehealth.hr.domain.entity.VacationEntity.State.APROVADA
  )
ORDER BY v.id DESC
""")
    List<VacationPendingDto> findLastApprovedVacationPerEmployee();




}
