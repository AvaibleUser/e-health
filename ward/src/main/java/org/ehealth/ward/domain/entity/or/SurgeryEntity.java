package org.ehealth.ward.domain.entity.or;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import org.ehealth.ward.domain.entity.finance.BillItemEntity;
import org.ehealth.ward.domain.entity.finance.TariffEntity;
import org.ehealth.ward.domain.entity.ward.PatientEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity(name = "surgery")
@Table(name = "surgery", schema = "operating_room")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class SurgeryEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private LocalDate performedDate;

    private String description;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private PatientEntity patient;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "tariff_id")
    private TariffEntity tariff;

    @OneToMany(mappedBy = "surgery")
    private Set<SurgerySpecialist> specialists;

    @OneToMany(mappedBy = "surgery")
    private Set<BillItemEntity> billItems;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
