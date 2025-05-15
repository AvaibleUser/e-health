package org.ehealth.ward.repository.finance;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.finance.bill.BillDto.NativeBillDto;
import org.ehealth.ward.domain.entity.finance.BillEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import feign.Param;

@Repository
public interface BillRepository extends JpaRepository<BillEntity, Long> {

    <T> Page<T> findAllByPatientId(long patientId, Pageable pageable, Class<T> type);

    <T> Optional<T> findByIdAndPatientId(long id, long patientId, Class<T> type);

    <T> Optional<T> findByPatientIdAndIsClosedFalse(long patientId, Class<T> type);

    boolean existsByPatientIdAndIsClosedFalse(long patientId);

    @Query(nativeQuery = true, value = """
            SELECT b.id,
                SUM(CASE
                        WHEN bi.type = 'HOSPITALIZED' THEN 200 * DATE_PART('day', AGE(COALESCE(a.discharge_date, CURRENT_DATE), a.admission_date))
                        WHEN bi.type = 'SURGERY' THEN t.price
                        WHEN bi.type = 'CONSULTATION' THEN 20
                        ELSE 0
                    END),
                b.is_closed,
                b.is_paid,
                b.created_at,
                b.updated_at
            FROM finance.bill b
                LEFT JOIN finance.bill_item bi ON b.id = bi.bill_id
                LEFT JOIN operating_room.surgery s ON bi.surgery_id = s.id
                LEFT JOIN ward.admission a ON bi.admission_id = a.id
                LEFT JOIN finance.tariff t ON s.tariff_id = t.id
            WHERE b.patient_id = :patientId
                AND b.is_closed != TRUE
            GROUP BY b.id,
                b.is_closed,
                b.is_paid,
                b.created_at,
                b.updated_at
            ORDER BY b.created_at DESC
            """)
    List<NativeBillDto> findAllByPatientIdAndIsClosedFalse(@Param("patientId") long patientId);
}
