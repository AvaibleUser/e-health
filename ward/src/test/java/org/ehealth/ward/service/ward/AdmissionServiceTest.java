package org.ehealth.ward.service.ward;

import static org.ehealth.ward.domain.entity.finance.BillItemEntity.BillItemType.HOSPITALIZED;
import static org.ehealth.ward.util.ThenMockAlias.thenMock;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.ehealth.ward.domain.dto.ward.admission.AddAdmissionDto;
import org.ehealth.ward.domain.dto.ward.admission.AdmissionDto;
import org.ehealth.ward.domain.dto.ward.admission.UpdateAdmissionDto;
import org.ehealth.ward.domain.entity.finance.BillEntity;
import org.ehealth.ward.domain.entity.finance.BillItemEntity;
import org.ehealth.ward.domain.entity.ward.AdmissionEntity;
import org.ehealth.ward.domain.entity.ward.PatientEntity;
import org.ehealth.ward.domain.entity.ward.RoomEntity;
import org.ehealth.ward.repository.finance.BillItemRepository;
import org.ehealth.ward.repository.finance.BillRepository;
import org.ehealth.ward.repository.ward.AdmissionRepository;
import org.ehealth.ward.repository.ward.PatientRepository;
import org.ehealth.ward.repository.ward.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdmissionServiceTest {

    @Mock
    private AdmissionRepository admissionRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BillRepository billRepository;

    @Mock
    private BillItemRepository billItemRepository;

    @InjectMocks
    private AdmissionService admissionService;

    @Test
    void addAdmission() {
        // given
        long patientId = 1L;
        long roomId = 2L;
        PatientEntity patient = PatientEntity.builder()
                .fullName("Este es un paciente")
                .cui("123456789")
                .build();
        RoomEntity room = RoomEntity.builder()
                .number("1")
                .admissions(Set.of())
                .build();
        AddAdmissionDto addAdmissionDto = AddAdmissionDto.builder()
                .admissionDate(LocalDate.now())
                .roomId(roomId)
                .build();
        AdmissionEntity expectedAdmission = AdmissionEntity.builder()
                .admissionDate(addAdmissionDto.admissionDate())
                .patient(patient)
                .room(room)
                .build();
        BillEntity expectedBill = BillEntity.builder()
                .patient(patient)
                .isClosed(true)
                .isPaid(true)
                .build();
        BillItemEntity expectedItem = BillItemEntity.builder()
                .concept("Hospitalización en el cuarto '1' el " + addAdmissionDto.admissionDate())
                .amount(BigDecimal.ONE)
                .type(HOSPITALIZED)
                .bill(expectedBill)
                .admission(expectedAdmission)
                .build();

        given(admissionRepository.findByPatientIdAndIsAdmitted(patientId)).willReturn(Optional.empty());
        given(patientRepository.findById(patientId)).willReturn(Optional.of(patient));
        given(roomRepository.findById(roomId)).willReturn(Optional.of(room));
        given(billRepository.findByPatientIdAndIsClosedFalse(patientId, BillEntity.class)).willReturn(Optional.empty());

        // when
        admissionService.addAdmission(patientId, addAdmissionDto);

        // then
        thenMock(billRepository).should().save(refEq(expectedBill));
        thenMock(billItemRepository).should().save(refEq(expectedItem));
    }

    @Test
    void updateAdmission() {
        // given
        long patientId = 1L;
        long newRoom = 2L;
        long id = newRoom;
        Optional<LocalDate> now = Optional.of(LocalDate.now());
        PatientEntity patient = PatientEntity.builder()
                .fullName("Este es un paciente")
                .cui("123456789")
                .build();
        AdmissionDto admissionDto = AdmissionDto.builder()
                .id(id)
                .build();
        AdmissionEntity admission = AdmissionEntity.builder()
                .admissionDate(LocalDate.now())
                .patient(patient)
                .room(RoomEntity.builder().id(1L).number("1").build())
                .build();
        UpdateAdmissionDto inAdmission = UpdateAdmissionDto.builder()
                .dischargeDate(now)
                .roomId(Optional.of(newRoom))
                .build();
        RoomEntity room = RoomEntity.builder()
                .id(newRoom)
                .number("1")
                .admissions(Set.of())
                .build();
        AdmissionEntity expectedAdmission = admission.toBuilder()
                .dischargeDate(now.get())
                .room(room)
                .build();

        given(admissionRepository.findByPatientIdAndIsAdmitted(patientId)).willReturn(Optional.of(admissionDto));
        given(admissionRepository.findByIdAndPatientId(id, patientId, AdmissionEntity.class))
                .willReturn(Optional.of(admission.toBuilder().build()));
        given(roomRepository.findById(newRoom)).willReturn(Optional.of(room));

        // when
        admissionService.updateAdmission(id, patientId, inAdmission);

        // then
        thenMock(admissionRepository).should().save(refEq(expectedAdmission));
    }
}
