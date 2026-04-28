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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        // Use real market timestamp from event; fall back to now() if not provided
        asset.setLastUpdate(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());

        assetRepository.save(asset);

        marketPriceCacheService.updatePrice(dto.getTicker(), dto.getPrice());

        log.info("Ticker {} updated successfully", dto.getTicker());
    }

    public List<AssetDTO> findAllActive() {
        // fix: query directly by status instead of loading all and filtering in memory
        return assetRepository.findAllByStatus(AssetStatus.ACTIVE).stream()
                .map(AssetDTO::fromEntity)
                .toList();
    }

    public Optional<AssetDTO> findByTicker(String ticker) {
        // fix: only return ACTIVE assets — inactive assets return empty (404)
        return assetRepository.findByTickerAndStatus(ticker.toUpperCase(), AssetStatus.ACTIVE)
                .map(AssetDTO::fromEntity);
    }
}