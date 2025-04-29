package org.ehealth.ward.domain.entity.ward;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import org.ehealth.ward.domain.entity.finance.BillEntity;
import org.ehealth.ward.domain.entity.or.SurgeryEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "patient")
@Table(name = "patient", schema = "ward")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String fullName;

    @NonNull
    @Column(nullable = false, unique = true)
    private String cui;

    private LocalDate birthDate;

    private String phone;

    private String email;

    @OneToMany(mappedBy = "patient")
    private Set<AdmissionEntity> admissions;

    @OneToMany(mappedBy = "patient")
    private Set<SurgeryEntity> surgeries;

    @OneToMany(mappedBy = "patient")
    private Set<BillEntity> bills;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
