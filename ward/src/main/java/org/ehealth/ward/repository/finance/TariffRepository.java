package org.ehealth.ward.repository.finance;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.entity.finance.TariffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository extends JpaRepository<TariffEntity, Long> {

    <T> Optional<T> findById(Long id, Class<T> type);

    <T> List<T> findAllBy(Class<T> type);
}
