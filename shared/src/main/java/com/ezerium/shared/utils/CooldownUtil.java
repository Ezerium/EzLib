package com.ezerium.shared.utils;

import java.util.HashMap;
import java.util.Map;

public class CooldownUtil {
    private static final Map<String, Long> COOLDOWN_MAP = new HashMap<>();

    public static boolean isOnCooldown(String key, int cooldownTime) {
        if (COOLDOWN_MAP.containsKey(key)) {
            long time = COOLDOWN_MAP.get(key);
            long now = System.currentTimeMillis();
            return now - time < cooldownTime;
        }

        return false;
    }

    public static void setCooldown(String key) {
        COOLDOWN_MAP.put(key, System.currentTimeMillis());
    }

    public static void removeCooldown(String key) {
        COOLDOWN_MAP.remove(key);
    }

    public static void clearCooldowns() {
        COOLDOWN_MAP.clear();
    }

    static {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30 * 1000);
                } catch (InterruptedException e) {
                    continue;
                }
                COOLDOWN_MAP.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue() > 1000 * 60 * 60 * 24);
            }
        }).start();
    }
}
