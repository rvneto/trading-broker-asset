package com.rvneto.broker.asset.controller;

import com.rvneto.broker.asset.dto.AssetDTO;
import com.rvneto.broker.asset.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Endpoints for querying catalog assets.")
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    @Operation(
            summary = "List all active assets",
            description = "Returns a list of assets available for trading."
    )
    public ResponseEntity<List<AssetDTO>> getAllActiveAssets() {
        return ResponseEntity.ok(assetService.findAllActive());
    }

    @GetMapping("/{ticker}")
    @Operation(
            summary = "Get asset details by ticker",
            description = "Returns full asset details from the database. Intended for frontend use."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset found"),
            @ApiResponse(responseCode = "404", description = "Asset not found or inactive")
    })
    public ResponseEntity<AssetDTO> getByTicker(@PathVariable String ticker) {
        return ResponseEntity.ok(assetService.findByTicker(ticker));
    }

    @GetMapping("/{ticker}/price")
    @Operation(
            summary = "Get current price by ticker",
            description = "Returns the current price from Redis cache. Falls back to DB on cache miss. Intended for internal service use."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price found"),
            @ApiResponse(responseCode = "404", description = "Asset not found or inactive")
    })
    public ResponseEntity<Map<String, Object>> getPriceByTicker(@PathVariable String ticker) {
        BigDecimal price = assetService.getPriceByTicker(ticker);
        return ResponseEntity.ok(Map.of(
                "ticker", ticker.toUpperCase(),
                "price", price
        ));
    }
}
