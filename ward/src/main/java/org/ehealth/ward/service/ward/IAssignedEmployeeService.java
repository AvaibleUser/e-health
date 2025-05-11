package org.ehealth.ward.service.ward;

import org.ehealth.ward.domain.dto.ward.AssignedEmployeeReportDto;

import java.time.LocalDate;
import java.util.List;

public interface IAssignedEmployeeService {
    List<AssignedEmployeeReportDto> getAssignedDoctorsReport(LocalDate startDate, LocalDate endDate);
}
