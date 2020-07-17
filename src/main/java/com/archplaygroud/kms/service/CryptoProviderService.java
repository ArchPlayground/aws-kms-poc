package com.archplaygroud.kms.service;

import com.amazonaws.encryptionsdk.CryptoMaterialsManager;
import com.amazonaws.encryptionsdk.MasterKeyProvider;
import com.amazonaws.encryptionsdk.caching.CachingCryptoMaterialsManager;
import com.amazonaws.encryptionsdk.caching.LocalCryptoMaterialsCache;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.encryptionsdk.multi.MultipleProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.archplaygroud.kms.common.CryptoConstants.MAX_CACHE_AGE;

@Service
@Slf4j
public class CryptoProviderService {

    @Autowired
    private LocalCryptoMaterialsCache globalKMSCache;

    @Autowired
    private CompanyIDtoKeyIDService companyIDtoKeyIDService;

    @Autowired
    private Environment environment;

    @Cacheable("cryptoProvider")
    public CryptoMaterialsManager companySpecificKeyProvider(String companyId) {
        log.info("Creating cryptoProvider for company: {}", companyId);
        String[] keyIds = companyIDtoKeyIDService.getKeyIds(companyId);
        log.info("Key count for company id {}  is  {}", companyId, keyIds.length);

        return CachingCryptoMaterialsManager.newBuilder()
                .withMaxAge(MAX_CACHE_AGE, TimeUnit.HOURS)
                .withMasterKeyProvider(getKeyProvider(keyIds))
                .withCache(globalKMSCache)
                .build();
    }

    private MasterKeyProvider<?> getKeyProvider(String[] keyIds) {
        if(keyIds.length == 1) {
            // return single key provider
            // IMP: On prem decryptors must always fall in this if condition
            log.info("Created single key provider for active profile: {} ", environment.getActiveProfiles()[0]);
            return KmsMasterKeyProvider.builder().withKeysForEncryption(keyIds[0]).build();
        } else {
            // return multi key providers
            // IMP: this should only happen on Encryptor or Cloud decryptors
            List<MasterKeyProvider<?>> keyProviders =
                Arrays.asList(keyIds)
                        .stream()
                        .map(keyId -> KmsMasterKeyProvider.builder().withKeysForEncryption(keyId).build())
                        .collect(Collectors.toList());
            log.info("Created multiple key providers for active profile: {}",environment.getActiveProfiles()[0]);
            return MultipleProviderFactory.buildMultiProvider(keyProviders);
        }
    }
}
