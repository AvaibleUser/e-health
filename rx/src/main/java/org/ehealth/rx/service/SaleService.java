package org.ehealth.rx.service;

import lombok.RequiredArgsConstructor;
import org.ehealth.rx.repository.SaleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaleService implements ISaleService {
    private final SaleRepository saleRepository;
}
