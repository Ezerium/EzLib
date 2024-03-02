package com.ezerium.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

    public static String[] shift(String[] array) {
        String[] newArray = new String[array.length - 1];
        System.arraycopy(array, 1, newArray, 0, newArray.length);
        return newArray;
    }

    public static String[] arrayOf(String... strings) {
        return strings;
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (characters.length() * Math.random());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }

}
