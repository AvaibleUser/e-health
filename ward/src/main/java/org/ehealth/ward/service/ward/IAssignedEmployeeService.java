package org.ehealth.ward.service.ward;

import org.ehealth.ward.domain.dto.ward.employee.AssignedEmployeeReportDto;
import org.ehealth.ward.domain.dto.ward.employee.CompleteEmployeeDto;
import java.time.LocalDate;
import java.util.List;

public interface IAssignedEmployeeService {
    List<AssignedEmployeeReportDto> getAssignedDoctorsReport(LocalDate startDate, LocalDate endDate);

    List<CompleteEmployeeDto> getAssignableEmployees();

    List<CompleteEmployeeDto> getAssignedEmployees(long patientId, long admissionId);

    void assignEmployees(long patientId, long admissionId, List<Long> employeeIds);
}
