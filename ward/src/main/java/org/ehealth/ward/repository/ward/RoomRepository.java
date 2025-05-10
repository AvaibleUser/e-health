package org.ehealth.ward.repository.ward;

import java.util.Optional;

import org.ehealth.ward.domain.dto.ward.room.RoomDto;
import org.ehealth.ward.domain.entity.ward.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    <T> Optional<T> findById(Long id, Class<T> type);

    <T> Page<T> findAllBy(Pageable pageable, Class<T> type);

    @Query("""
            SELECT new org.ehealth.ward.domain.dto.ward.room.RoomDto(
                r.id,
                r.number,
                r.costPerDay,
                r.isOccupied,
                r.underMaintenance,
                r.createdAt,
                r.updatedAt
            )
            FROM room r
            WHERE (SELECT COUNT(a) > 0 FROM admission a WHERE a.room.id = r.id AND a.dischargeDate IS NOT NULL AND a.dischargeDate < CURRENT_DATE)
                AND r.underMaintenance = TRUE
            """)
    Page<RoomDto> findAllFiltered(Pageable pageable);
}
