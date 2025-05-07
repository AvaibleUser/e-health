package org.ehealth.hr.domain.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "contract")
@Table(name = "contract", schema = "hr")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class ContractEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private BigDecimal salary;

    private BigDecimal igssDiscount;

    private BigDecimal irtraDiscount;

    @Enumerated(EnumType.STRING)
    private TerminationReason terminationReason;

    private String terminationDescription;

    @NonNull
    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employee;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public static enum TerminationReason {
        AUMENTO_SALARIAL,
        DESPIDO,
        FIN_CONTRATO,
        REDUCCION_SALARIAL,
        NUEVO_CONTRATO
    }
}
