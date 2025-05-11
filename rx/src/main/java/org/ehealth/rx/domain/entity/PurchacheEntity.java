package org.ehealth.rx.domain.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity(name = "purchase")
@Table(name = "purchase", schema = "rx")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class PurchacheEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private Integer quantity;

    @NonNull
    @Column(nullable = false)
    private BigDecimal unitCost;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "medicine_id")
    private MedicineEntity medicine;

    @CreationTimestamp
    private Instant purchasedAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
