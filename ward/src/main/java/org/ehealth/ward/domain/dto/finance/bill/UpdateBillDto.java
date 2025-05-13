package org.ehealth.ward.domain.dto.finance.bill;

import java.util.Optional;

import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateBillDto(
        Optional<Boolean> isClosed,
        Optional<Boolean> isPaid) {
}
