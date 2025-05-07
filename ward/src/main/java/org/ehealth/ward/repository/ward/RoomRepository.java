package org.ehealth.ward.repository.ward;

import org.ehealth.ward.domain.entity.ward.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

}
