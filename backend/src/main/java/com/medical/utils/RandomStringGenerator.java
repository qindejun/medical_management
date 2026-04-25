package com.medical.utils;

import java.security.SecureRandom;
import java.util.stream.IntStream;

public class RandomStringGenerator {
    public static void main(String[] args) {
        String randomString = generateRandomString(16);
        System.out.println(randomString);
    }

    public static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        return IntStream.range(0, length)
                .mapToObj(i -> String.valueOf(characters.charAt(random.nextInt(characters.length()))))
                .collect(java.util.stream.Collectors.joining());
    }
}
    