package org.ehealth.ward.service.or;

import org.ehealth.ward.domain.dto.or.surgery.SurgeryPaymentDto;
import org.ehealth.ward.repository.or.SurgeryRepository;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SurgeryServiceTest {

    @Mock
    private SurgeryRepository surgeryRepository;

    @InjectMocks
    private SurgeryService surgeryService;

    private static final Long VALID_EMPLOYEE_ID = 1L;
    private static final Long VALID_SURGERY_ID = 10L;
    private static final String VALID_DESCRIPTION = "Appendectomy";
    private static final BigDecimal VALID_SPECIALIST_FEE = new BigDecimal("1500.00");
    private static final LocalDate VALID_PERFORMED_DATE = LocalDate.of(2023, 5, 15);

    @BeforeEach
    void setUp() {

    }

    /**
     * tests function getSurgeryPaymentDto
     */
    @Test
    void shouldReturnListOfSurgeryPaymentsWhenRepositoryReturnsData() {
        // given
        List<SurgeryPaymentDto> expectedPayments = List.of(
                buildValidSurgeryPaymentDto(),
                buildValidSurgeryPaymentDto().toBuilder()
                        .id(2L)
                        .description("Hernia Repair")
                        .specialistFee(new BigDecimal("2000.00"))
                        .build()
        );

        given(surgeryRepository.findAllSurgeryPaymentsBySpecialistType())
                .willReturn(expectedPayments);

        // when
        List<SurgeryPaymentDto> result = surgeryService.getSurgeryPaymentDto();

        // then
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactlyElementsOf(expectedPayments);

        verify(surgeryRepository).findAllSurgeryPaymentsBySpecialistType();
    }

    @Test
    void shouldReturnEmptyListWhenRepositoryReturnsNoData() {
        // given
        given(surgeryRepository.findAllSurgeryPaymentsBySpecialistType())
                .willReturn(Collections.emptyList());

        // when
        List<SurgeryPaymentDto> result = surgeryService.getSurgeryPaymentDto();

        // then
        assertThat(result).isEmpty();
        verify(surgeryRepository).findAllSurgeryPaymentsBySpecialistType();
    }

    @Test
    void shouldHandleNullResponseFromRepositoryGracefully() {
        // given
        given(surgeryRepository.findAllSurgeryPaymentsBySpecialistType())
                .willReturn(null);

        // when
        List<SurgeryPaymentDto> result = surgeryService.getSurgeryPaymentDto();

        // then
        assertThat(result).isNull();
        verify(surgeryRepository).findAllSurgeryPaymentsBySpecialistType();
    }

    /**
     * tests function existSurgeryPayment
     */
    @Test
    void shouldReturnTrueWhenSurgeryPaymentExists() {
        // given
        given(surgeryRepository.existsById(VALID_EMPLOYEE_ID))
                .willReturn(true);

        // when
        boolean result = surgeryService.existSurgeryPayment(VALID_EMPLOYEE_ID);

        // then
        assertThat(result).isTrue();
        verify(surgeryRepository).existsById(VALID_EMPLOYEE_ID);
    }

    @Test
    void shouldReturnFalseWhenSurgeryPaymentDoesNotExist() {
        // given
        given(surgeryRepository.existsById(VALID_EMPLOYEE_ID))
                .willReturn(false);

        // when
        boolean result = surgeryService.existSurgeryPayment(VALID_EMPLOYEE_ID);

        // then
        assertThat(result).isFalse();
        verify(surgeryRepository).existsById(VALID_EMPLOYEE_ID);
    }

    @Test
    void shouldHandleNullIdGracefully() {
        // given
        given(surgeryRepository.existsById(null))
                .willReturn(false);

        // when
        boolean result = surgeryService.existSurgeryPayment(null);

        // then
        assertThat(result).isFalse();
        verify(surgeryRepository).existsById(null);
    }

    @Test
    void shouldReturnFalseForNonExistentId() {
        // given
        Long NON_EXISTENT_ID = 999L;
        given(surgeryRepository.existsById(NON_EXISTENT_ID))
                .willReturn(false);

        // when
        boolean result = surgeryService.existSurgeryPayment(NON_EXISTENT_ID);

        // then
        assertThat(result).isFalse();
        verify(surgeryRepository).existsById(NON_EXISTENT_ID);
    }

    private SurgeryPaymentDto buildValidSurgeryPaymentDto() {
        return SurgeryPaymentDto.builder()
                .id(VALID_SURGERY_ID)
                .description(VALID_DESCRIPTION)
                .performedDate(VALID_PERFORMED_DATE)
                .employeeId(VALID_EMPLOYEE_ID)
                .specialistFee(VALID_SPECIALIST_FEE)
                .build();
    }



}