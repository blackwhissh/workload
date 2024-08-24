package com.blackwhissh.workload.utils;

import java.util.Random;
import java.util.logging.Logger;

public class GeneratorUtils {
    private final static String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Logger logger = Logger.getLogger(GeneratorUtils.class.getName());
    private static int serialNumber = 0;
    private static StringBuilder sb;

    public static String generateUsername(String firstName, String lastName, boolean exists) {
        sb = new StringBuilder();
        logger.info("Generating Username");
        sb.append(firstName).append(".").append(lastName);
        if (exists) {
            logger.info("Appending Serial Number to the Username");
            sb.append(serialNumber);
            serialNumber++;
        }
        return sb.toString();
    }

    public static String generatePassword() {
        Random random = new Random();
        sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        return sb.toString();
    }
}

