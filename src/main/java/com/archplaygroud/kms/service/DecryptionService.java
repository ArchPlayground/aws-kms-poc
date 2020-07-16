package com.archplaygroud.kms.service;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoMaterialsManager;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.MasterKeyProvider;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.function.Function;

import static com.archplaygroud.kms.common.CryptoConstants.ENCRYPTION_CONTEXT_KEY;

@Service
@Slf4j
@Profile({"decryptor"})
public class DecryptionService {


    @Autowired
    private MasterKeyProvider<?> mainCloudKeyProvider;
    @Autowired
    private CryptoMaterialsManager cachedCryptoProvider;

    private final AwsCrypto cryptoHandler;
    private final Function<byte[], CryptoResult<byte[], ?>> cloudKeyDecryptor;
    private final Function<byte[], CryptoResult<byte[], ?>> cachedKeyDecryptor;

    public DecryptionService(){
        cryptoHandler = new AwsCrypto();
        cloudKeyDecryptor = (decodedEncryptedData) -> {
            log.info("Using Main cloud key for decryption");
            return cryptoHandler.decryptData(mainCloudKeyProvider, decodedEncryptedData);
        };
        cachedKeyDecryptor = (decodedEncryptedData) -> {
            log.info("Using Cached key for decryption");
            return cryptoHandler.decryptData(cachedCryptoProvider, decodedEncryptedData);
        };
    }

    /**
     * Given a companyId and Base64 encoded encrypted path
     * Returns a plaintext decrypted path using Main cloud key
     * @param companyId
     * @param encryptedPath
     * @return bas64EncodedEncryptedPath
     */
    @SneakyThrows
    public String decryptPath(String companyId, String encryptedPath, boolean useCachedProvider){
        log.info("Going to decrypt : {}, for company: {}", encryptedPath, companyId);
        // get decrypted path
        byte[] decodedEncryptedPathBytes = Base64.getDecoder().decode(encryptedPath);
        CryptoResult<byte[], ?> decryptionResult =
                useCachedProvider ?
                cachedKeyDecryptor.apply(decodedEncryptedPathBytes) :
                cachedKeyDecryptor.apply(decodedEncryptedPathBytes);

        // throw an exception if incorrect company id
        if (!Objects.equals(decryptionResult.getEncryptionContext().get(ENCRYPTION_CONTEXT_KEY), companyId)) {
            log.error("Invalid company id found. Expected {}, Found {}",
                    decryptionResult.getEncryptionContext().get(ENCRYPTION_CONTEXT_KEY), companyId);
            throw new IllegalArgumentException("Company Id does not match");
        }
        log.info("Decryption successful.");

        return new String(decryptionResult.getResult(), StandardCharsets.UTF_8);
    }
}
