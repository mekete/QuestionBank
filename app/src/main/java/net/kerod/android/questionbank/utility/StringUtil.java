package net.kerod.android.questionbank.utility;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.util.Random;
import java.util.UUID;

public class StringUtil {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ALPHANUMERIC_LENGTH = ALPHANUMERIC.length();

    // ---------------------------------------------------------------------------------------------
    public static String generateShortUuid() {
        return generateShortUuid(8);
    }

    public static String generateShortUuid(int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            builder.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC_LENGTH)));
        }
        return builder.toString();
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }


    public static boolean isNullOrEmpty(@NonNull String string) {
        return TextUtils.isEmpty(string) || string.equalsIgnoreCase("null");
    }

    @NonNull
    public static String safeTrim(@NonNull String string) {
        return isNullOrEmpty(string) ? "" : string.toString().trim();
    }

    public static boolean isIntegerConvertible(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    public static boolean isDoubleConvertible(String string) {
        try {
            Double.valueOf(string);
            return true;
        } catch (Exception ex) {
        }
        return false;
    }


    public static boolean isFloatConvertible(String string) {
        try {
            Float.valueOf(string);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    //TODO implement
    public static boolean isValidEmail(String email) {
        return true;
    }
}
