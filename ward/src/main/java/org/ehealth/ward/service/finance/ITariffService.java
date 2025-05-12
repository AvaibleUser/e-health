package org.ehealth.ward.service.finance;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.finance.tariff.TariffDto;

public interface ITariffService {

    Optional<TariffDto> findById(long id);

    List<TariffDto> findAllTariffs();
}
