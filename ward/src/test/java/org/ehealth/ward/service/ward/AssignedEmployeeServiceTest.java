package org.ehealth.ward.service.ward;

import org.ehealth.ward.domain.dto.ward.employee.AssignedEmployeeReportDto;
import org.ehealth.ward.repository.ward.AssignedEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AssignedEmployeeServiceTest {

    @Mock
    private AssignedEmployeeRepository assignedEmployeeRepository;

    @InjectMocks
    private AssignedEmployeeService assignedEmployeeService;

    @BeforeEach
    void setUp() {

    }

    private static final LocalDate VALID_START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate VALID_END_DATE = LocalDate.of(2023, 12, 31);
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusYears(1);

    @Test
    void shouldReturnAllDoctorsAssignedWhenDatesAreNull() {
        // given
        List<AssignedEmployeeReportDto> expectedDoctors = buildAssignedDoctorsList(3);
        given(assignedEmployeeRepository.findALLDoctorsAssigned()).willReturn(expectedDoctors);

        // when
        List<AssignedEmployeeReportDto> result = assignedEmployeeService.getAssignedDoctorsReport(null, null);

        // then
        assertThat(result).isEqualTo(expectedDoctors);
        verify(assignedEmployeeRepository).findALLDoctorsAssigned();
        verify(assignedEmployeeRepository, never()).findDoctorsAssignedInPeriod(any(), any());
    }

    @Test
    void shouldReturnDoctorsAssignedInPeriodWhenDatesAreValid() {
        // given
        List<AssignedEmployeeReportDto> expectedDoctors = buildAssignedDoctorsList(2);
        given(assignedEmployeeRepository.findDoctorsAssignedInPeriod(VALID_START_DATE, VALID_END_DATE))
                .willReturn(expectedDoctors);

        // when
        List<AssignedEmployeeReportDto> result = assignedEmployeeService.getAssignedDoctorsReport(VALID_START_DATE, VALID_END_DATE);

        // then
        assertThat(result).isEqualTo(expectedDoctors);
        verify(assignedEmployeeRepository).findDoctorsAssignedInPeriod(VALID_START_DATE, VALID_END_DATE);
        verify(assignedEmployeeRepository, never()).findALLDoctorsAssigned();
    }

    @Test
    void shouldReturnEmptyListWhenNoDoctorsFoundInPeriod() {
        // given
        given(assignedEmployeeRepository.findDoctorsAssignedInPeriod(VALID_START_DATE, VALID_END_DATE))
                .willReturn(Collections.emptyList());

        // when
        List<AssignedEmployeeReportDto> result = assignedEmployeeService.getAssignedDoctorsReport(VALID_START_DATE, VALID_END_DATE);

        // then
        assertThat(result).isEmpty();
    }


    @Test
    void shouldReturnAllDoctorsWhenOnlyStartDateIsNull() {
        // given
        List<AssignedEmployeeReportDto> expectedDoctors = buildAssignedDoctorsList(3);
        given(assignedEmployeeRepository.findALLDoctorsAssigned()).willReturn(expectedDoctors);

        // when
        List<AssignedEmployeeReportDto> result = assignedEmployeeService.getAssignedDoctorsReport(null, VALID_END_DATE);

        // then
        assertThat(result).isEqualTo(expectedDoctors);
        verify(assignedEmployeeRepository).findALLDoctorsAssigned();
    }

    @Test
    void shouldReturnAllDoctorsWhenOnlyEndDateIsNull() {
        // given
        List<AssignedEmployeeReportDto> expectedDoctors = buildAssignedDoctorsList(3);
        given(assignedEmployeeRepository.findALLDoctorsAssigned()).willReturn(expectedDoctors);

        // when
        List<AssignedEmployeeReportDto> result = assignedEmployeeService.getAssignedDoctorsReport(VALID_START_DATE, null);

        // then
        assertThat(result).isEqualTo(expectedDoctors);
        verify(assignedEmployeeRepository).findALLDoctorsAssigned();
    }


    private List<AssignedEmployeeReportDto> buildAssignedDoctorsList(int count) {
        List<AssignedEmployeeReportDto> doctors = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            doctors.add(buildAssignedDoctor(i));
        }
        return doctors;
    }

    private AssignedEmployeeReportDto buildAssignedDoctor(long id) {
        return AssignedEmployeeReportDto.builder()
                .employeeId(id)
                .admissionDate(VALID_START_DATE.plusDays(id))
                .dischargeDate(VALID_END_DATE.minusDays(id))
                .fullName("Doctor " + id)
                .cui("CUI" + id)
                .build();
    }
}