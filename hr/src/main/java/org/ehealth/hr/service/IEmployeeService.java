package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.CreateEmployeeDto;
import org.ehealth.hr.domain.dto.EmployeeResponseDto;
import org.ehealth.hr.domain.dto.reports.ReportAssignedEmployeeDto;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.dto.EmployeeDto;

import java.time.LocalDate;
import java.util.List;

public interface IEmployeeService {

    EmployeeResponseDto createEmployee(CreateEmployeeDto dto);
    EmployeeDto findEmployeeByCui(String cui);
    List<EmployeeDto> findAllEmployeesOrdered();
    List<EmployeeDto> findEmployeesByIds(List<Long> byIds);
    List<EmployeeDto> findEmployeesByArea(Long areaId);
    List<EmployeeDto> findAssignableEmployees();
    ReportAssignedEmployeeDto getReportAssignedEmployeeInRange(Integer filter, String startDate, String endDate);
    boolean existEmployeeById(Long id);
}
