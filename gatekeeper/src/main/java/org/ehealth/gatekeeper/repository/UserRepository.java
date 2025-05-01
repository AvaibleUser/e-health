package org.ehealth.gatekeeper.repository;

import java.util.Optional;

import org.ehealth.gatekeeper.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByCui(String cui);

    Optional<?> findUnknownById(long id, Class<?> type);

    <U> Optional<U> findByEmail(String email, Class<U> type);
}
