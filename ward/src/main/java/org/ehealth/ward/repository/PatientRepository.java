package org.ehealth.ward.repository;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.patient.PatientDto;
import org.ehealth.ward.domain.entity.ward.PatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import feign.Param;

public interface PatientRepository extends JpaRepository<PatientEntity, Long>, JpaSpecificationExecutor<PatientEntity> {

    boolean existsByCui(String cui);

    <T> Optional<T> findById(Long id, Class<T> type);

    <T> Page<T> findAllPageableBy(Pageable pageable, Class<T> type);

    @Query("""
            SELECT new org.ehealth.ward.domain.dto.patient.PatientDto(
                p.id,
                p.fullName,
                p.cui,
                p.birthDate,
                p.phone,
                p.email,
                p.createdAt,
                p.updatedAt
            )
            FROM patient p
            WHERE p.fullName LIKE CONCAT('%', :filter, '%')
                OR p.cui LIKE CONCAT('%', :filter, '%')
                OR p.phone LIKE CONCAT('%', :filter, '%')
                OR p.email LIKE CONCAT('%', :filter, '%')
            """)
    Page<PatientDto> findAllWithSearch(@Param("filter") String filter, Pageable pageable);

    <T> List<T> findAllByOrderByCreatedAtDesc(Class<T> type);
}
