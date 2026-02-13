package com.placideh.rateLimiter.util;


import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class ApiKeyGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

 // format: sk_live_{32_random_characters}
    public String generateApiKey() {
        byte[] randomBytes = new byte[24]; // 24 bytes = 32 base64 characters
        secureRandom.nextBytes(randomBytes);

        String randomPart = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        return Constants.API_KEY_PREFIX + randomPart;
    }
}

