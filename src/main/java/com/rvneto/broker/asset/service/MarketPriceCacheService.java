package com.rvneto.broker.asset.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketPriceCacheService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private static final String CACHE_KEY_PREFIX = "market:price:";

    private final StringRedisTemplate redisTemplate;

    public void updatePrice(String ticker, BigDecimal price) {
        String key = CACHE_KEY_PREFIX + ticker;
        redisTemplate.opsForValue().set(key, price.toString(), CACHE_TTL);
        log.debug("Redis cache updated: {} = R$ {}", key, price);
    }

    public Optional<BigDecimal> getPrice(String ticker) {
        String key = CACHE_KEY_PREFIX + ticker;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            log.debug("Redis cache miss for key: {}", key);
            return Optional.empty();
        }
        log.debug("Redis cache hit for key: {} = R$ {}", key, value);
        return Optional.of(new BigDecimal(value));
    }
}
