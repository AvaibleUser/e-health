package org.ehealth.ward.domain.entity.finance;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.time.Instant;

import org.ehealth.ward.domain.entity.or.SurgeryEntity;
import org.ehealth.ward.domain.entity.ward.AdmissionEntity;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "bill_item")
@Table(name = "bill_item", schema = "finance")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class BillItemEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String concept;

    private BigDecimal amount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BillItemType type = BillItemType.CONSULTATION;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "admission_id")
    private AdmissionEntity admission;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "surgery_id")
    private SurgeryEntity surgery;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "bill_id")
    private BillEntity bill;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public static enum BillItemType {
        HOSPITALIZED,
        SURGERY,
        CONSULTATION
    }
}
