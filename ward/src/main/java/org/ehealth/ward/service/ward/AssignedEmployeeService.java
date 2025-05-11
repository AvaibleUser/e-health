package org.ehealth.ward.service.ward;

import lombok.RequiredArgsConstructor;
import org.ehealth.ward.domain.dto.ward.AssignedEmployeeReportDto;
import org.ehealth.ward.repository.ward.AssignedEmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignedEmployeeService implements IAssignedEmployeeService {

    private final AssignedEmployeeRepository assignedEmployeeRepository;

    @Override
    public List<AssignedEmployeeReportDto> getAssignedDoctorsReport(LocalDate startDate, LocalDate endDate) {
       if (startDate == null || endDate == null) {
           return this.assignedEmployeeRepository.findALLDoctorsAssigned();
       }
        return assignedEmployeeRepository.findDoctorsAssignedInPeriod(startDate, endDate);
    }
}

