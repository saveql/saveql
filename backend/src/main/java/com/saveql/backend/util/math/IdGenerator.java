package com.saveql.backend.util.math;

import java.security.SecureRandom;
import java.util.Base64;

public class IdGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefhiklmnopqrstuvwxz0123456789";
    private static final int LENGTH = 32;

    public static String generate() {
        return generate(LENGTH);
    }

    public static String generate(int length) {
        byte[] randomBytes = new byte[length];
        RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public static String key() {
        StringBuilder key = new StringBuilder(LENGTH + LENGTH / 4);

        for (int i = 0; i < LENGTH; i++) {
            if (i > 0 && i % 4 == 0) key.append("-");
            key.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        return key.toString();
    }

    public static String generate(String name) {
        StringBuilder code = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(index);
            code.append(randomChar);
        }

        return name + "_" + code;
    }
}
