package com.bryan.finance.utilities;

import java.security.SecureRandom;

public class PasswordFactory {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final int PASSWORD_SEGMENT = 4;
    private static SecureRandom random = new SecureRandom();

    public static String generatePassword() {
        String letterGroup = generateLettersSegment();
        String numberGroup = generateNumbersSegment();

        return letterGroup + numberGroup;
    }

    private static String generateLettersSegment() {
        StringBuilder sb = new StringBuilder(PASSWORD_SEGMENT);
        for (int i = 0; i < PASSWORD_SEGMENT; i++)
            sb.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        return sb.toString();
    }

    private static String generateNumbersSegment() {
        StringBuilder sb = new StringBuilder(PASSWORD_SEGMENT);
        for (int i = 0; i < PASSWORD_SEGMENT; i++)
            sb.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        return sb.toString();
    }
}
