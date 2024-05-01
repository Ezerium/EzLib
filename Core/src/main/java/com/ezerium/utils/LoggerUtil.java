package com.ezerium.utils;

import java.util.logging.Logger;

public class LoggerUtil {

    public static final Logger LOGGER = Logger.getLogger("Ezerium");

    public static void debug(Object message) {
        LOGGER.fine(message.toString());
    }

    public static void info(Object message) {
        LOGGER.info(message.toString());
    }

    public static void warn(Object message) {
        LOGGER.warning(message.toString());
    }

    public static void err(Object message) {
        LOGGER.severe(message.toString());
    }

}
