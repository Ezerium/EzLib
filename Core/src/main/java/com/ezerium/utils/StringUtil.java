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

}
