package com.rvneto.broker.asset.messaging;

import com.rvneto.broker.asset.dto.AssetMarketDataDTO;
import com.rvneto.broker.asset.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketDataConsumer {

    private final AssetService assetService;

    @KafkaListener(
            topics = "trading-assets-market-data-v1",
            groupId = "trading-broker-asset"
    )
    public void consume(AssetMarketDataDTO dto) {
        log.info("Market data received for ticker: {}", dto.getTicker());
        try {
            assetService.updateAsset(dto);
        } catch (Exception e) {
            log.error("Failed to process market data for ticker {}: {}", dto.getTicker(), e.getMessage(), e);
            // Rethrow so Kafka can apply retry/DLQ policy instead of silently discarding
            throw e;
        }
    }
}