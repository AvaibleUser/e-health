package org.ehealth.ward.repository.or;

import java.util.List;

import org.ehealth.ward.domain.entity.or.SurgerySpecialistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurgerySpecialistRepository extends JpaRepository<SurgerySpecialistEntity, Long> {

    <T> List<T> findAllBySurgeryPatientIdAndSurgeryId(Long patientId, Long surgeryId, Class<T> type);

    Long deleteBySurgeryPatientIdAndSurgeryId(Long patientId, Long surgeryId);
}
