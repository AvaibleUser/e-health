package org.ehealth.ward.repository.ward;

import java.time.LocalDate;
import java.util.List;

import org.ehealth.ward.domain.dto.ward.employee.AssignedEmployeeReportDto;
import org.ehealth.ward.domain.entity.ward.AssignedEmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignedEmployeeRepository extends JpaRepository<AssignedEmployeeEntity, Long> {

    <T> List<T> findByAdmissionPatientIdAndAdmissionId(Long patientId, Long admissionId, Class<T> type);

    Long deleteByAdmissionPatientIdAndAdmissionId(Long patientId, Long admissionId);

    @Query("""
            SELECT new org.ehealth.ward.domain.dto.ward.employee.AssignedEmployeeReportDto(
                ae.employeeId,
                a.admissionDate,
                a.dischargeDate,
                p.fullName,
                p.cui
            )
            FROM assigned_employee ae
            JOIN ae.admission a
            JOIN a.patient p
            WHERE ae.type = org.ehealth.ward.domain.entity.ward.AssignedEmployeeEntity.AssignedEmployeeType.DOCTOR
              AND a.admissionDate BETWEEN :startDate AND :endDate
            ORDER BY a.admissionDate DESC
            """)
    List<AssignedEmployeeReportDto> findDoctorsAssignedInPeriod(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT new org.ehealth.ward.domain.dto.ward.employee.AssignedEmployeeReportDto(
                ae.employeeId,
                a.admissionDate,
                a.dischargeDate,
                p.fullName,
                p.cui
            )
            FROM assigned_employee ae
            JOIN ae.admission a
            JOIN a.patient p
            WHERE ae.type = org.ehealth.ward.domain.entity.ward.AssignedEmployeeEntity.AssignedEmployeeType.DOCTOR
            ORDER BY a.admissionDate DESC
            """)
    List<AssignedEmployeeReportDto> findALLDoctorsAssigned();

}
