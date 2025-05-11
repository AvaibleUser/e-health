package org.ehealth.rx.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ehealth.rx.domain.dto.CreateSaleDto;
import org.ehealth.rx.service.ISaleService;
import org.ehealth.rx.util.annotation.CurrentUserCui;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final ISaleService saleService;

    @PostMapping()
    public ResponseEntity<Void> createSaleTotal(
            @CurrentUserCui String cui,
            @Valid @RequestBody CreateSaleDto createSaleDto
    ) {
        saleService.createSaleTotal(cui, createSaleDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
