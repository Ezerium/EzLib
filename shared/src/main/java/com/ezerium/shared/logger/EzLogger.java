package com.ezerium.shared.logger;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EzLogger {

    public static void log(String message) {
        System.out.println(message);
    }

    public static void logError(String message) {
        System.err.println(message);
    }

    public static void logError(String message, Throwable throwable) {
        System.err.println(message);
        throwable.printStackTrace();
    }

    public static void logWarning(String message) {
        System.out.println("WARNING: " + message);
    }

    public static void logInfo(String message) {
        System.out.println("INFO: " + message);
    }

    public static void debug(String message) {
        System.out.println("\u001B[34m[DEBUG] \u001B[37m" + message);
    }

}
