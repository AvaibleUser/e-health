package org.ehealth.ward.service.finance;

import lombok.RequiredArgsConstructor;
import org.ehealth.ward.domain.dto.finance.report.BillItemReportDto;
import org.ehealth.ward.domain.dto.finance.report.ReportIncomeBill;
import org.ehealth.ward.repository.finance.BillItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillItemService implements IBillItemService {
    private final BillItemRepository billItemRepository;

    @Override
    public ReportIncomeBill getReportIncomeBill(List<BillItemReportDto> items) {
        if (items == null || items.isEmpty()) {
            return ReportIncomeBill.builder()
                    .totalIncome(BigDecimal.ZERO)
                    .items(Collections.emptyList())
                    .build();
        }

        BigDecimal totalIncome = items.stream()
                .map(BillItemReportDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportIncomeBill.builder()
                .totalIncome(totalIncome)
                .items(items)
                .build();
    }

    @Override
    public ReportIncomeBill getReportIncomeBillInRange(LocalDate startDate, LocalDate endDate) {
        List<BillItemReportDto> items;

        if (startDate == null || endDate == null) {
            items = this.billItemRepository.findAllByOrderByCreatedAtDesc(BillItemReportDto.class);
            return this.getReportIncomeBill(items);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atStartOfDay(zoneId).toInstant();

        items = billItemRepository.findAllByCreatedAtBetweenOrderByCreatedAtDesc(startInstant,endInstant, BillItemReportDto.class);

        return this.getReportIncomeBill(items);

    }

}
