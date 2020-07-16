package com.archplaygroud.kms.service;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoMaterialsManager;
import com.amazonaws.encryptionsdk.MasterKeyProvider;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

import static com.archplaygroud.kms.common.CryptoConstants.ENCRYPTION_CONTEXT_KEY;

@Service
@Slf4j
@Profile({"encryptor"})
public class EncryptionService {


    @Autowired
    private MasterKeyProvider<?> mainCloudKeyProvider;
    @Autowired
    private CryptoMaterialsManager cachedCryptoProvider;

    private final AwsCrypto cryptoHandler;
    private final BiFunction<byte[], Map<String,String>, byte[]> cloudKeyEncryptor;
    private final BiFunction<byte[], Map<String,String>, byte[]> cachedKeyEncryptor;

    public EncryptionService(){
        cryptoHandler = new AwsCrypto();
        cloudKeyEncryptor = (unencryptedData, encryptionContext) -> {
            log.info("Using Main cloud key for encryption");
            return cryptoHandler.encryptData(mainCloudKeyProvider, unencryptedData, encryptionContext).getResult();
        };
        cachedKeyEncryptor = (unencryptedData, encryptionContext) -> {
            log.info("Using Cached key for encryption");
            return cryptoHandler.encryptData(cachedCryptoProvider, unencryptedData, encryptionContext).getResult();
        };
    }

    /**
     * Given a companyId, plaintext path
     * Returns Base64 encoded encrypted path encrypted using Main cloud key
     * @param companyId
     * @param path
     * @return bas64EncodedEncryptedPath
     */
    public String encryptPath(String companyId, String path, boolean useCachedProvider) {
        log.info("Going to encrypt : {}, for company: {}", path, companyId);

        // set encryption context and convert path to bytes
        Map<String, String> encryptionContext =
                Collections.singletonMap(ENCRYPTION_CONTEXT_KEY, companyId);
        byte[] pathData = path.getBytes(StandardCharsets.UTF_8);

        // get encrypted path in Base64 encoded string
        byte[] encryptedPath =
                useCachedProvider ?
                cachedKeyEncryptor.apply(pathData, encryptionContext) :
                cloudKeyEncryptor.apply(pathData, encryptionContext);

        log.info("Encryption successful.");

        return Base64.getEncoder().encodeToString(encryptedPath);
    }
}
