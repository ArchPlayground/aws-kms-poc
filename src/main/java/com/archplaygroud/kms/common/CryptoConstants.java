package com.archplaygroud.kms.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CryptoConstants {
    public static final String ENCRYPTION_CONTEXT_KEY = "companyId";
    public static final String COMPANY_A_ID = "002";
    public static final int MAX_CACHE_CAPACITY = 200;
    public static final int MAX_CACHE_AGE = 24;
    public static final String ONPREM_KEY_ENV_VAR_NAME = "kms.keyId";
}
