package com.archplaygroud.kms.config;

import com.amazonaws.encryptionsdk.caching.LocalCryptoMaterialsCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import static com.archplaygroud.kms.common.CryptoConstants.MAX_CACHE_CAPACITY;

@Configuration
@Slf4j
public class CryptoConfig{

    @Bean
    public LocalCryptoMaterialsCache cryptoMaterialsCache() {
        log.info("Creating new cache instance with Capacity : {}", MAX_CACHE_CAPACITY);
        return new LocalCryptoMaterialsCache(MAX_CACHE_CAPACITY);
    }

}
