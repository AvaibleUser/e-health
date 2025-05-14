package org.ehealth.ward.domain.dto.ward.room;

import lombok.Builder;
import lombok.Generated;
import org.ehealth.ward.domain.entity.ward.RoomEntity;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record RoomResponseDto(
        Long id,
        String number,
        BigDecimal costPerDay,
        boolean isOccupied,
        boolean underMaintenance,
        Instant createdAt,
        Instant updatedAt
) {
    @Generated
    public static RoomResponseDto fromEntity(RoomEntity entity) {
        return RoomResponseDto.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .costPerDay(entity.getCostPerDay())
                .isOccupied(entity.isOccupied())
                .underMaintenance(entity.isUnderMaintenance())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
