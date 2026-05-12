package com.rvneto.broker.asset.dto;

import com.rvneto.broker.asset.domain.Asset;
import com.rvneto.broker.asset.domain.AssetStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AssetDTO(
        String ticker,
        String name,
        BigDecimal currentPrice,
        LocalDateTime lastUpdate,
        AssetStatus status
) {
    public static AssetDTO fromEntity(Asset asset) {
        return new AssetDTO(
                asset.getTicker(),
                asset.getName(),
                asset.getCurrentPrice(),
                asset.getLastUpdate(),
                asset.getStatus()
        );
    }
}