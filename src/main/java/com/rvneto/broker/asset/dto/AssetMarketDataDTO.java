package com.rvneto.broker.asset.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetMarketDataDTO {

    private String ticker;
    private String name;
    private BigDecimal price;
    private Long volume;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}