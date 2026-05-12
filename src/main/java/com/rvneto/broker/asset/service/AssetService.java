package com.rvneto.broker.asset.service;

import com.rvneto.broker.asset.domain.Asset;
import com.rvneto.broker.asset.domain.AssetStatus;
import com.rvneto.broker.asset.dto.AssetDTO;
import com.rvneto.broker.asset.dto.AssetMarketDataDTO;
import com.rvneto.broker.asset.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final AssetRepository assetRepository;
    private final MarketPriceCacheService marketPriceCacheService;

    @Transactional
    public void updateAsset(AssetMarketDataDTO dto) {
        log.info("Updating ticker {} with price R$ {}", dto.getTicker(), dto.getPrice());

        Asset asset = assetRepository.findByTicker(dto.getTicker())
                .orElse(Asset.builder()
                        .ticker(dto.getTicker())
                        .status(AssetStatus.ACTIVE)
                        .build());

        asset.setName(dto.getName());
        asset.setCurrentPrice(dto.getPrice());
        asset.setLastUpdate(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());

        assetRepository.save(asset);

        marketPriceCacheService.updatePrice(dto.getTicker(), dto.getPrice());

        log.info("Ticker {} updated successfully", dto.getTicker());
    }

    public List<AssetDTO> findAllActive() {
        return assetRepository.findAllByStatus(AssetStatus.ACTIVE).stream()
                .map(AssetDTO::fromEntity)
                .toList();
    }

    public AssetDTO findByTicker(String ticker) {
        // Full details — always reads from DB (used by frontend)
        return assetRepository.findByTickerAndStatus(ticker.toUpperCase(), AssetStatus.ACTIVE)
                .map(AssetDTO::fromEntity)
                .orElseThrow(() -> new NoSuchElementException("Asset not found: " + ticker.toUpperCase()));
    }

    public BigDecimal getPriceByTicker(String ticker) {
        // Cache-first — used by internal services (order-api) to avoid DB hits
        return marketPriceCacheService.getPrice(ticker.toUpperCase())
                .orElseGet(() -> {
                    log.info("Cache miss for {}. Fetching from DB and populating cache.", ticker.toUpperCase());
                    Asset asset = assetRepository.findByTickerAndStatus(ticker.toUpperCase(), AssetStatus.ACTIVE)
                            .orElseThrow(() -> new NoSuchElementException("Asset not found or inactive: " + ticker.toUpperCase()));
                    marketPriceCacheService.updatePrice(asset.getTicker(), asset.getCurrentPrice());
                    return asset.getCurrentPrice();
                });
    }
}
