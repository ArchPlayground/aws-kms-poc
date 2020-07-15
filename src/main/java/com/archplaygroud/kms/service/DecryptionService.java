package com.archplaygroud.kms.service;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import static com.archplaygroud.kms.common.CryptoConstants.ENCRYPTION_CONTEXT_KEY;

@Service
@Slf4j
public class DecryptionService {

    private final AwsCrypto cryptoHandler;

    @Autowired
    private KmsMasterKeyProvider mainCloudKeyProvider;

    public DecryptionService(){
        cryptoHandler = new AwsCrypto();
    }

    /**
     * Given a companyId and Base64 encoded encrypted path
     * Returns a plaintext decrypted path using Main cloud key
     * @param companyId
     * @param encryptedPath
     * @return bas64EncodedEncryptedPath
     */
    @SneakyThrows
    public String decryptPathWithSingleKey(String companyId, String encryptedPath){
        log.info("Going to decrypt : {}, for company: {}", encryptedPath, companyId);
        // get decrypted path
        byte[] decodedEncryptedPathBytes = Base64.getDecoder().decode(encryptedPath);
        CryptoResult<byte[], ?> decryptionResult = new AwsCrypto().decryptData(mainCloudKeyProvider, decodedEncryptedPathBytes);

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
