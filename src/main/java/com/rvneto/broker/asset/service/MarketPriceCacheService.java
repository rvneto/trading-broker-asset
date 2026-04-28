package com.rvneto.broker.asset.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketPriceCacheService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private static final String CACHE_KEY_PREFIX = "market:price:";

    private final StringRedisTemplate redisTemplate;

    public void updatePrice(String ticker, BigDecimal price) {
        String key = CACHE_KEY_PREFIX + ticker;
        // TTL of 10 minutes — prevents stale prices for inactive or delisted assets
        redisTemplate.opsForValue().set(key, price.toString(), CACHE_TTL);
        log.debug("Redis cache updated: {} = R$ {}", key, price);
    }
}
