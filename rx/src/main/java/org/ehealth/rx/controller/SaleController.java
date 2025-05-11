package org.ehealth.rx.controller;

import lombok.RequiredArgsConstructor;
import org.ehealth.rx.service.ISaleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/sales")
@RequiredArgsConstructor
public class SaleController {
    private final ISaleService saleService;
}
