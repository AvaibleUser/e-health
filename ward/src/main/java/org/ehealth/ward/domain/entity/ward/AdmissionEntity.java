package org.ehealth.ward.domain.entity.ward;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import org.ehealth.ward.domain.entity.finance.BillItemEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "admission")
@Table(name = "admission", schema = "ward")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class AdmissionEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private LocalDate admissionDate;

    private LocalDate dischargeDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AdmissionStatus status = AdmissionStatus.ADMITTED;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private PatientEntity patient;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @OneToMany(mappedBy = "admission")
    private Set<AssignedEmployeeEntity> assignedEmployees;

    @OneToMany(mappedBy = "admission")
    private Set<BillItemEntity> billItems;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public static enum AdmissionStatus {
        ADMITTED,
        DISCHARGED
    }
}
