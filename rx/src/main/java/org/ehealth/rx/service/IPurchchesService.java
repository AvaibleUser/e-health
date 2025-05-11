package org.ehealth.rx.service;

import org.ehealth.rx.domain.dto.CreatePurchacheDto;

public interface IPurchchesService {

    void create(Long medicineId, CreatePurchacheDto createPurchacheDto);
}
