package org.ehealth.hr.service;

import feign.FeignException;
import org.ehealth.hr.client.PatientClient;
import org.ehealth.hr.domain.dto.EmployeeDto;
import org.ehealth.hr.domain.dto.or.PaymentPerSurgeryDto;
import org.ehealth.hr.domain.dto.or.SpecialistPaymentDto;
import org.ehealth.hr.domain.dto.or.SurgeryPaymentDto;
import org.ehealth.hr.domain.entity.AreaEntity;
import org.ehealth.hr.domain.entity.EmployeeEntity;
import org.ehealth.hr.domain.entity.SpecialistPaymentEntity;
import org.ehealth.hr.domain.exception.RequestConflictException;
import org.ehealth.hr.domain.exception.ValueNotFoundException;
import org.ehealth.hr.repository.EmployeeRepository;
import org.ehealth.hr.repository.SpecialistPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class SpecialistPaymentServiceTest {


    private static final Long VALID_EMPLOYEE_ID = 1L;
    private static final Long VALID_SURGERY_ID = 100L;
    private static final Long INVALID_EMPLOYEE_ID = 999L;
    private static final String VALID_FULL_NAME = "Dr. Elvis Aguilar";
    private static final String VALID_EMAIL = "elvis@test.com";
    private static final String VALID_CUI = "1234567890123";
    private static final String VALID_DESCRIPTION = "Appendectomy";
    private static final LocalDate VALID_PERFORMED_DATE = LocalDate.now().minusDays(1);
    private static final BigDecimal VALID_SPECIALIST_FEE = new BigDecimal("1500.00");

    private static final Long VALID_AREA_ID = 1L;
    private static final PaymentPerSurgeryDto VALID_PAYMENT_DTO = buildPaymentPerSurgeryDto();
    private static final EmployeeEntity VALID_EMPLOYEE_ENTITY = buildValidEmployeeEntity();
    private static final SpecialistPaymentEntity SAVED_PAYMENT_ENTITY = buildSavedPaymentEntity();


    @Mock
    private SpecialistPaymentRepository specialistPaymentRepository;

    @Mock
    private PatientClient patientClient;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private SpecialistPaymentService specialistPaymentService;

    @BeforeEach
    void setUp() {
       // VALID_PAYMENT_DTO = buildPaymentPerSurgeryDto();
    }

    /**
     * tests function getPaymentPerSurgery
     */
    @Test
    void shouldReturnListOfUnpaidSurgeriesWhenThereAreUnpaidSurgeries() {
        // given
        SurgeryPaymentDto unpaidSurgery = buildSurgeryPaymentDto(VALID_SURGERY_ID, VALID_EMPLOYEE_ID);
        EmployeeDto specialistEmployee = buildEmployeeDto(VALID_EMPLOYEE_ID, true);
        SpecialistPaymentDto paidSpecialistPayment = buildSpecialistPaymentDto(VALID_SURGERY_ID + 1, VALID_EMPLOYEE_ID + 1);

        given(patientClient.getSurgeryPayments())
                .willReturn(List.of(unpaidSurgery));
        given(employeeRepository.findAllByIsSpecialistTrueOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(List.of(specialistEmployee));
        given(specialistPaymentRepository.findAllSpecialistPayments())
                .willReturn(List.of(paidSpecialistPayment));

        // when
        List<PaymentPerSurgeryDto> result = specialistPaymentService.getPaymentPerSurgery();

        // then
        assertThat(result).hasSize(1);
        PaymentPerSurgeryDto paymentDto = result.get(0);
        assertThat(paymentDto.id()).isEqualTo(VALID_SURGERY_ID);
        assertThat(paymentDto.employeeId()).isEqualTo(VALID_EMPLOYEE_ID);
        assertThat(paymentDto.fullName()).isEqualTo(VALID_FULL_NAME);
        assertThat(paymentDto.Cui()).isEqualTo(VALID_CUI);
        assertThat(paymentDto.specialistFee()).isEqualTo(VALID_SPECIALIST_FEE);
    }

    @Test
    void shouldReturnEmptyListWhenAllSurgeriesAreAlreadyPaid() {
        // given
        SurgeryPaymentDto paidSurgery = buildSurgeryPaymentDto(VALID_SURGERY_ID, VALID_EMPLOYEE_ID);
        SpecialistPaymentDto specialistPayment = buildSpecialistPaymentDto(VALID_SURGERY_ID, VALID_EMPLOYEE_ID);

        given(patientClient.getSurgeryPayments())
                .willReturn(List.of(paidSurgery));
        given(employeeRepository.findAllByIsSpecialistTrueOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(List.of(buildEmployeeDto(VALID_EMPLOYEE_ID, true)));
        given(specialistPaymentRepository.findAllSpecialistPayments())
                .willReturn(List.of(specialistPayment));

        // when
        List<PaymentPerSurgeryDto> result = specialistPaymentService.getPaymentPerSurgery();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenNoSurgeriesExist() {
        // given
        given(patientClient.getSurgeryPayments())
                .willReturn(Collections.emptyList());
        given(employeeRepository.findAllByIsSpecialistTrueOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(List.of(buildEmployeeDto(VALID_EMPLOYEE_ID, true)));
        given(specialistPaymentRepository.findAllSpecialistPayments())
                .willReturn(Collections.emptyList());

        // when
        List<PaymentPerSurgeryDto> result = specialistPaymentService.getPaymentPerSurgery();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotIncludeNonSpecialistEmployees() {
        // given
        SurgeryPaymentDto surgery = buildSurgeryPaymentDto(VALID_SURGERY_ID, VALID_EMPLOYEE_ID);
        EmployeeDto nonSpecialistEmployee = buildEmployeeDto(VALID_EMPLOYEE_ID, false);

        given(patientClient.getSurgeryPayments())
                .willReturn(List.of(surgery));
        given(employeeRepository.findAllByIsSpecialistTrueOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(Collections.emptyList());
        given(specialistPaymentRepository.findAllSpecialistPayments())
                .willReturn(Collections.emptyList());

        // when
        List<PaymentPerSurgeryDto> result = specialistPaymentService.getPaymentPerSurgery();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowRequestConflictExceptionWhenFeignClientFails() {
        // given
        given(patientClient.getSurgeryPayments())
                .willThrow(FeignException.class);

        // when / then
        assertThatThrownBy(() -> specialistPaymentService.getPaymentPerSurgery())
                .isInstanceOf(RequestConflictException.class)
                .hasMessageContaining("No se ha podido obtener las cirugias");
    }

    @Test
    void shouldNotIncludeSurgeriesWithNonExistentSpecialist() {
        // given
        SurgeryPaymentDto surgeryWithInvalidSpecialist = buildSurgeryPaymentDto(VALID_SURGERY_ID, INVALID_EMPLOYEE_ID);
        EmployeeDto validSpecialist = buildEmployeeDto(VALID_EMPLOYEE_ID, true);

        given(patientClient.getSurgeryPayments())
                .willReturn(List.of(surgeryWithInvalidSpecialist));
        given(employeeRepository.findAllByIsSpecialistTrueOrderByCreatedAtDesc(EmployeeDto.class))
                .willReturn(List.of(validSpecialist));
        given(specialistPaymentRepository.findAllSpecialistPayments())
                .willReturn(Collections.emptyList());

        // when
        List<PaymentPerSurgeryDto> result = specialistPaymentService.getPaymentPerSurgery();

        // then
        assertThat(result).isEmpty();
    }


    /**
     * tests function createPaymentPerSurgery
     */
    @Test
    void shouldCreatePaymentWhenAllConditionsAreMet() {
        // given
        given(employeeRepository.findById(VALID_EMPLOYEE_ID))
                .willReturn(Optional.of(VALID_EMPLOYEE_ENTITY));
        given(patientClient.existSurge(VALID_SURGERY_ID))
                .willReturn(true);
        given(specialistPaymentRepository.save(any(SpecialistPaymentEntity.class)))
                .willReturn(SAVED_PAYMENT_ENTITY);

        // when
        specialistPaymentService.createPaymentPerSurgery(VALID_PAYMENT_DTO);

        // then
        verify(specialistPaymentRepository).save(argThat(entity ->
                entity.getSurgeryId().equals(VALID_SURGERY_ID) &&
                        entity.getAmount().equals(VALID_SPECIALIST_FEE) &&
                        entity.getSpecialistDoctor().getId().equals(VALID_EMPLOYEE_ID)
        ));
    }

    @Test
    void shouldThrowValueNotFoundExceptionWhenEmployeeNotFound() {
        // given
        given(employeeRepository.findById(INVALID_EMPLOYEE_ID))
                .willReturn(Optional.empty());

        PaymentPerSurgeryDto invalidDto = VALID_PAYMENT_DTO.toBuilder()
                .employeeId(INVALID_EMPLOYEE_ID)
                .build();

        // when / then
        assertThatThrownBy(() -> specialistPaymentService.createPaymentPerSurgery(invalidDto))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessageContaining("Empleado especialista no encontrado");
    }

    @Test
    void shouldThrowRequestConflictExceptionWhenFeignClientFailsTs() {
        // given
        given(employeeRepository.findById(VALID_EMPLOYEE_ID))
                .willReturn(Optional.of(VALID_EMPLOYEE_ENTITY));
        given(patientClient.existSurge(VALID_SURGERY_ID))
                .willThrow(FeignException.class);

        // when / then
        assertThatThrownBy(() -> specialistPaymentService.createPaymentPerSurgery(VALID_PAYMENT_DTO))
                .isInstanceOf(RequestConflictException.class)
                .hasMessageContaining("No se ha podido encontrar la cirugia");
    }

    @Test
    void shouldThrowValueNotFoundExceptionWhenSurgeryDoesNotExist() {
        // given
        given(employeeRepository.findById(VALID_EMPLOYEE_ID))
                .willReturn(Optional.of(VALID_EMPLOYEE_ENTITY));
        given(patientClient.existSurge(VALID_SURGERY_ID))
                .willReturn(false);

        // when / then
        assertThatThrownBy(() -> specialistPaymentService.createPaymentPerSurgery(VALID_PAYMENT_DTO))
                .isInstanceOf(ValueNotFoundException.class)
                .hasMessageContaining("La cirugia que intenta pagar no existe");
    }

    // metodos para ayuda, como utils
    private static PaymentPerSurgeryDto buildPaymentPerSurgeryDto() {
        return PaymentPerSurgeryDto.builder()
                .id(VALID_SURGERY_ID)
                .employeeId(VALID_EMPLOYEE_ID)
                .specialistFee(VALID_SPECIALIST_FEE)
                .description(VALID_DESCRIPTION)
                .performedDate(VALID_PERFORMED_DATE)
                .fullName(VALID_FULL_NAME)
                .Cui(VALID_CUI)
                .build();
    }

    private static EmployeeEntity buildValidEmployeeEntity() {
        return EmployeeEntity.builder()
                .id(VALID_EMPLOYEE_ID)
                .fullName(VALID_FULL_NAME)
                .cui(VALID_CUI)
                .email(VALID_EMAIL)
                .area(AreaEntity.builder()
                        .id(VALID_AREA_ID)
                        .name("Test Area")
                        .build())
                .isSpecialist(true)
                .build();
    }

    private static SpecialistPaymentEntity buildSavedPaymentEntity() {
        return SpecialistPaymentEntity.builder()
                .id(1L)
                .surgeryId(VALID_SURGERY_ID)
                .amount(VALID_SPECIALIST_FEE)
                .specialistDoctor(buildValidEmployeeEntity())
                .build();
    }

    private SurgeryPaymentDto buildSurgeryPaymentDto(Long surgeryId, Long employeeId) {
        return SurgeryPaymentDto.builder()
                .id(surgeryId)
                .employeeId(employeeId)
                .description(VALID_DESCRIPTION)
                .performedDate(VALID_PERFORMED_DATE)
                .specialistFee(VALID_SPECIALIST_FEE)
                .build();
    }

    private EmployeeDto buildEmployeeDto(Long employeeId, boolean isSpecialist) {
        return EmployeeDto.builder()
                .id(employeeId)
                .fullName(VALID_FULL_NAME)
                .cui(VALID_CUI)
                .isSpecialist(isSpecialist)
                .build();
    }

    private SpecialistPaymentDto buildSpecialistPaymentDto(Long surgeryId, Long employeeId) {
        return SpecialistPaymentDto.builder()
                .surgeryId(surgeryId)
                .employeeId(employeeId)
                .build();
    }

}