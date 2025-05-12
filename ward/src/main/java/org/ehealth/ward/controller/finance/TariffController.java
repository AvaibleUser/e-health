package org.ehealth.ward.controller.finance;

import java.util.List;

import org.ehealth.ward.domain.dto.finance.tariff.TariffDto;
import org.ehealth.ward.service.finance.ITariffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/tariffs")
@RequiredArgsConstructor
public class TariffController {

    private final ITariffService tariffService;

    @GetMapping
    public List<TariffDto> findAllTariffs() {
        return tariffService.findAllTariffs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TariffDto> findById(@PathVariable long id) {
        return ResponseEntity.of(tariffService.findById(id));
    }
}
