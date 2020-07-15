package com.archplaygroud.kms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig{

    @Value("${kms.cloud.main.keyId}")
    private String mainCloudKeyId;

    @Bean
    public KmsMasterKeyProvider mainCloudKeyProvider(){
        return KmsMasterKeyProvider.builder().withKeysForEncryption(mainCloudKeyId).build();
    }

}
