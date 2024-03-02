package com.ezerium.config;

import com.ezerium.annotations.config.ConfigSection;
import com.ezerium.annotations.config.ConfigValue;
import com.ezerium.annotations.config.Configuration;
import com.ezerium.utils.DataUtil;
import com.ezerium.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigHandler {

    /**
     * The time in milliseconds to sleep between checking for changes in the configuration files.
     */
    public static int SLEEP_TIME = 5 * 1000;
    private final Map<String, DataUtil<Class<?>, Object>> configs;

    public ConfigHandler() {
        this.configs = new HashMap<>();

        new Thread(() -> {
            Map<String, Object> oldValue = new HashMap<>();
            while (true) {
                for (Map.Entry<String, DataUtil<Class<?>, Object>> entry : this.configs.entrySet()) {
                    Class<?> clazz = entry.getValue().getKey();
                    Object instance = entry.getValue().getValue();

                    for (Field field : clazz.getDeclaredFields()) {
                        if (!field.isAnnotationPresent(ConfigValue.class) || !field.isAnnotationPresent(ConfigSection.class)) continue;

                        Object value = ReflectionUtils.getFieldValue(instance, field.getName());
                        if (!Objects.equals(oldValue.getOrDefault(entry.getKey(), null), value)) {
                            oldValue.put(entry.getKey(), value);

                            update(field, value, instance, entry.getKey());
                        }
                    }
                }

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void update(Field field, Object value, Object instance, String configName) {
        configName = (configName.endsWith(".yml") || configName.endsWith(".json") ? configName : configName + ".yml");
        if (configName.endsWith(".yml")) {

        } else {

        }
    }

    /**
     * Registers a configuration class with the @Configuration annotation.
     * @param clazz The class to register.
     * @param instance An instance of the class (preferably store it in a field or variable).
     */
    public void registerConfig(Class<?> clazz, Object instance) {
        Configuration configuration = clazz.getAnnotation(Configuration.class);
        if (configuration == null) {
            throw new IllegalArgumentException("Class " + clazz.getSimpleName() + " does not have a @Configuration annotation.");
        }

        this.configs.put(configuration.value(), new DataUtil<>(clazz, instance));
    }
}
