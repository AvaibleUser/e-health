package org.ehealth.ward.domain.dto.ward.room;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record RoomDto(
        Long id,
        String number,
        BigDecimal costPerDay,
        boolean isOccupied,
        boolean underMaintenance,
        Instant createdAt,
        Instant updatedAt) {
}
