package com.rvneto.broker.asset.repository;

import com.rvneto.broker.asset.domain.Asset;
import com.rvneto.broker.asset.domain.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByTicker(String ticker);

    List<Asset> findAllByStatus(AssetStatus status);

    Optional<Asset> findByTickerAndStatus(String ticker, AssetStatus status);

}
