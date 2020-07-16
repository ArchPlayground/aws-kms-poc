package com.archplaygroud.kms.config;

import com.amazonaws.encryptionsdk.CryptoMaterialsManager;
import com.amazonaws.encryptionsdk.MasterKeyProvider;
import com.amazonaws.encryptionsdk.caching.CachingCryptoMaterialsManager;
import com.amazonaws.encryptionsdk.caching.LocalCryptoMaterialsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CryptoConfig{

    @Value("${kms.cloud.main.keyId}")
    private String mainCloudKeyId;


    @Bean
    public MasterKeyProvider<?> mainCloudKeyProvider(){
        return KmsMasterKeyProvider.builder().withKeysForEncryption(mainCloudKeyId).build();
    }

    @Bean
    @Autowired
    public CryptoMaterialsManager cachedKeyProvider(MasterKeyProvider<?> mainCloudKeyProvider) {
        // capacity determines how many company ids can it hold at same time
        LocalCryptoMaterialsCache cryptoCache = new LocalCryptoMaterialsCache(200);
        return CachingCryptoMaterialsManager.newBuilder()
                .withMaxAge(24, TimeUnit.HOURS)
                .withMasterKeyProvider(mainCloudKeyProvider)
                .withMessageUseLimit(10)
                .withCache(cryptoCache)
                .build();
    }

}
