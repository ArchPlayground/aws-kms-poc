package com.archplaygroud.kms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import static com.archplaygroud.kms.common.CryptoConstants.COMPANY_A_ID;
import static com.archplaygroud.kms.common.CryptoConstants.ONPREM_KEY_ENV_VAR_NAME;

@Service
@Slf4j
public class CompanyIDtoKeyIDService {
    // in actual application these values will be in a db
    private final String MAIN_KEY_ID = "arn:aws:kms:us-east-2:365010792203:key/e09eb48c-1a49-4762-b60b-9e17793f1962";
    private final String COMPA_KEY_ID = "arn:aws:kms:us-east-2:365010792203:key/758dd251-2b48-443b-8047-d3a6bddd0418";
    private final String COMPA_KEY_ID2 = "arn:aws:kms:us-east-2:365010792203:key/7b03fb7c-2d4b-4d88-9419-cc621a02fe42";

    @Autowired
    private Environment environment;

    /**
     * Given a company Id; Returns the Key corresponding to the company
     * Since this method will be called a lot in actual application
     * we will cache it's responses as responses will not change over lifetime of application
     * @param companyId
     * @return keyId
     */
    @Cacheable("keyIds")
    public String[] getKeyIds (String companyId) {
        // check if key is defined in config file - this will be case for on-prem vaults in production
        String keyIdFromEnv = environment.getProperty(ONPREM_KEY_ENV_VAR_NAME);
        if(keyIdFromEnv != null){
            // IMP: this must happen on on-prem Decryptor
            log.info("Got key from environment for active profile: {}" ,environment.getActiveProfiles()[0]);
            return new String[] { keyIdFromEnv };
        }
        // else lookup in DB and return the correct key (Skipping DB only for PoC)
        // Assuming the scenario here that a company may have more than one Vaults and
        // we want to maintain separate keys for each vault
        // IMP: this should only happen on Encryptor and Cloud Decryptor
        log.info("Got keys from db for active profile: {}", environment.getActiveProfiles()[0]);

        return COMPANY_A_ID.equals(companyId)
                ? new String[] { COMPA_KEY_ID, COMPA_KEY_ID2}
                : new String[] { MAIN_KEY_ID } ;
    }
}
