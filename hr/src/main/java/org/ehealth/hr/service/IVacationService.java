package org.ehealth.hr.service;

import org.ehealth.hr.domain.dto.vacation.CreateRequestVacationDto;
import org.ehealth.hr.domain.dto.vacation.UpdateRequestVacationDto;
import org.ehealth.hr.domain.dto.vacation.VacationPendingDto;
import org.ehealth.hr.domain.entity.EmployeeEntity;

import java.time.LocalDate;
import java.util.List;

public interface IVacationService {

    LocalDate addBusinessDays(LocalDate startDate, int businessDays);

    void createVacationWithEmployee(EmployeeEntity employee, LocalDate startDate);

    List<VacationPendingDto> findAllPendingVacations();

    List<VacationPendingDto> updatePendingVacations(Long vacationId, UpdateRequestVacationDto updateRequestVacationDto);

    VacationPendingDto createRequestVacation(CreateRequestVacationDto createRequestVacationDto);

    List<VacationPendingDto> findAllApprovedVacations();

}
