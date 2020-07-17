package com.archplaygroud.kms.service;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoMaterialsManager;
import com.amazonaws.encryptionsdk.CryptoResult;
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
/*
 Decryptor (Vault in production) can be run on cloud or on prem
 Any on prem Vault instance should be able to decrypt data with any key
 on prem Vaults will only be able to decrypt using their own key
 */
public class DecryptionService {

    @Autowired
    private CryptoProviderService cryptoProviderService;

    private final AwsCrypto cryptoHandler;

    public DecryptionService(){
        cryptoHandler = new AwsCrypto();
    }

    /**
     * Given a companyId and Base64 encoded encrypted path
     * Returns a plaintext decrypted path
     * // IMP: don't enable Spring caching on this method - we want this cached to be managed by KMS
     * @param companyId
     * @param encryptedPath
     * @return plainTestDecryptedPath
     */
    @SneakyThrows
    public String decryptPath(String companyId, String encryptedPath){
        log.info("Going to decrypt : {}, for company: {}", encryptedPath, companyId);
        CryptoMaterialsManager keyProvider = cryptoProviderService.companySpecificKeyProvider(companyId);

        // get decrypted path
        byte[] decodedEncryptedPathBytes = Base64.getDecoder().decode(encryptedPath);
        CryptoResult<byte[], ?> decryptionResult =
                cryptoHandler.decryptData(keyProvider, decodedEncryptedPathBytes);

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
