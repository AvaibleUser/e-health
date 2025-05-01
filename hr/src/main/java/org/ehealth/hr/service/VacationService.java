package org.ehealth.hr.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.entity.VacationEntity;
import org.ehealth.hr.repository.VacationRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VacationService implements IVacationService {
   private final VacationRepository vacationRepository;

   @Override
    public LocalDate addBusinessDays(LocalDate startDate, int businessDays) {
        LocalDate date = startDate;
        int addedDays = 0;

        while (addedDays < businessDays) {
            date = date.plusDays(1);
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                addedDays++;
            }
        }
        return date;
    }

    @Override
    public void createVacationWithEmployee(EmployeeEntity employee, LocalDate startDate) {
        LocalDate vacationStartDate = startDate.plusYears(1);
        LocalDate vacationEndDate = addBusinessDays(vacationStartDate, 15);

        VacationEntity vacation = VacationEntity.builder()
                .requestedDate(startDate)
                .startDate(vacationStartDate)
                .endDate(vacationEndDate)
                .approved(false)
                .finalized(false)
                .employee(employee)
                .build();

        vacationRepository.save(vacation);
    }
}
