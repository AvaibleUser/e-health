package org.ehealth.ward.service.finance;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.finance.tariff.TariffDto;
import org.ehealth.ward.repository.finance.TariffRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TariffService implements ITariffService {

    private final TariffRepository tariffRepository;

    @Override
    public Optional<TariffDto> findById(long id) {
        return this.tariffRepository.findById(id, TariffDto.class);
    }

    @Override
    public List<TariffDto> findAllTariffs() {
        return this.tariffRepository.findAllBy(TariffDto.class);
    }
}
