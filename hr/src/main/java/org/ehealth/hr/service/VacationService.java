package org.ehealth.hr.service;

import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.ehealth.hr.domain.dto.vacation.CreateRequestVacationDto;
import org.ehealth.hr.domain.dto.vacation.UpdateRequestVacationDto;
import org.ehealth.hr.domain.dto.vacation.VacationPendingDto;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.entity.VacationEntity;
import org.ehealth.hr.domain.exception.RequestConflictException;
import org.ehealth.hr.domain.exception.ValueNotFoundException;
import org.ehealth.hr.repository.EmployeeRepository;
import org.ehealth.hr.repository.VacationRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VacationService implements IVacationService {
   private final VacationRepository vacationRepository;
   private final EmployeeRepository employeeRepository;

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
                .employee(employee)
                .state(VacationEntity.State.PENDIENTE)
                .build();

        vacationRepository.save(vacation);
    }

    @Override
    public List<VacationPendingDto> findAllPendingVacations() {
        return vacationRepository.findAllPendingVacations();
    }

    @Override
    public List<VacationPendingDto> updatePendingVacations(Long vacationId, UpdateRequestVacationDto updateRequestVacationDto) {
       VacationEntity vacationEntity = vacationRepository.findById(vacationId)
               .orElseThrow(() -> new ValueNotFoundException("vacacion con id " + vacationId + " no encontrada"));

       vacationEntity.setApproved(updateRequestVacationDto.approved());
       if (updateRequestVacationDto.approved()){
           vacationEntity.setState(VacationEntity.State.APROVADA);
       }else{
           vacationEntity.setState(VacationEntity.State.RECHAZADA);
       }

       vacationRepository.save(vacationEntity);

       return vacationRepository.findAllPendingVacations();
   }

   @Override
   @Generated
   public VacationPendingDto createRequestVacation(CreateRequestVacationDto createRequestVacationDto) {
       EmployeeEntity employee = this.employeeRepository.findById(createRequestVacationDto.employeeId()).orElseThrow(()
               -> new ValueNotFoundException("Empleado no encontrado para la solicitud de vacaciones"));

       LocalDate vacationStartDate = createRequestVacationDto.startDate();

       LocalDate today = LocalDate.now();

       // Validación: mínimo 7 días de anticipación
       if (!today.isBefore(vacationStartDate.minusDays(6))) {
           throw new RequestConflictException("La solicitud debe realizarse con al menos 7 días de anticipación a la fecha de inicio.");
       }

       LocalDate vacationEndDate = addBusinessDays(vacationStartDate, createRequestVacationDto.days());

       VacationEntity vacation = VacationEntity.builder()
               .requestedDate(LocalDate.now())
               .startDate(vacationStartDate)
               .endDate(vacationEndDate)
               .approved(false)
               .employee(employee)
               .state(VacationEntity.State.PENDIENTE)
               .build();

       vacationRepository.save(vacation);

       return VacationPendingDto.builder()
               .id(vacation.getId())
               .name(employee.getArea().getName())
               .cui(employee.getCui())
               .fullName(employee.getFullName())
               .requestedDate(vacation.getRequestedDate())
               .approved(vacation.isApproved())
               .employeeId(employee.getId())
               .startDate(vacation.getStartDate())
               .endDate(vacation.getEndDate())
               .state(vacation.getState())
               .build();
   }

    @Override
    public List<VacationPendingDto> findAllApprovedVacations() {
        return vacationRepository.findLastApprovedVacationPerEmployee();
    }

}
