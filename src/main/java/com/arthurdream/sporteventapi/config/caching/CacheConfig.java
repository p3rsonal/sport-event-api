package com.arthurdream.sporteventapi.config.caching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    private final CacheProperties cacheProperties;

    @Autowired
    public CacheConfig(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Bean
    public CacheManager cacheManager() {
        if (cacheProperties.isEnabled()) {
            return new ConcurrentMapCacheManager("events", "event");
        } else {
            return new NoOpCacheManager();
        }
    }
}
