package com.ezerium.utils;

import java.util.logging.Logger;

public class LoggerUtil {

    public static final Logger LOGGER;

    public static void debug(String message) {
        LOGGER.fine(message);
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message) {
        LOGGER.warning(message);
    }

    public static void err(String message) {
        LOGGER.severe(message);
    }

    static {
        LOGGER = Logger.getLogger("Ezerium", "Ezerium");
    }

}
