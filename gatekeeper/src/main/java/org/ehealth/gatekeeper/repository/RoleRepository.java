package org.ehealth.gatekeeper.repository;

import java.util.Optional;

import org.ehealth.gatekeeper.domain.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    <T> Optional<T> findByName(String name, Class<T> entityClass);
}
