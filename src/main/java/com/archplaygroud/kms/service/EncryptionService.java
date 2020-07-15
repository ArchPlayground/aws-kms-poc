package com.archplaygroud.kms.service;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import static com.archplaygroud.kms.common.CryptoConstants.ENCRYPTION_CONTEXT_KEY;

@Service
@Slf4j
public class EncryptionService {

    private final AwsCrypto cryptoHandler;

    @Autowired
    private KmsMasterKeyProvider mainCloudKeyProvider;

    public EncryptionService(){
        cryptoHandler = new AwsCrypto();
    }

    /**
     * Given a companyId and plaintext path
     * Returns Base64 encoded encrypted path encrypted using Main cloud key
     * @param companyId
     * @param path
     * @return bas64EncodedEncryptedPath
     */
    public String encryptPathWithSingleKey(String companyId, String path){
        log.info("Going to encrypt : {}, for company: {}", path, companyId);

        // set encryption context and convert path to bytes
        Map<String, String> encryptionContext =
                Collections.singletonMap(ENCRYPTION_CONTEXT_KEY, companyId);
        byte[] pathData = path.getBytes(StandardCharsets.UTF_8);

        // get encrypted path in Base64 encoded string
        CryptoResult<byte[], KmsMasterKey> encryptionResult =
                cryptoHandler.encryptData(mainCloudKeyProvider, pathData, encryptionContext);
        byte[] encryptedPath = encryptionResult.getResult();
        log.info("Encryption successful.");

        return Base64.getEncoder().encodeToString(encryptedPath);
    }

}
