package org.ehealth.hr.service;

import org.ehealth.hr.domain.entity.EmployeeEntity;

import java.time.LocalDate;

public interface IVacationService {

    LocalDate addBusinessDays(LocalDate startDate, int businessDays);

    void createVacationWithEmployee(EmployeeEntity employee, LocalDate startDate);
}
