package com.ezerium.shared.config;

import com.ezerium.shared.utils.DataUtil;
import javafx.beans.value.ChangeListener;

import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    private final Map<String, DataUtil<Class<?>, ChangeListener<?>>> configs;

    public ConfigHandler() {
        this.configs = new HashMap<>();
    }

    public void registerConfig(Class<?> clazz) {
        ChangeListener<?> listener = (observable, oldValue, newValue) -> {

        };
        this.configs.put(clazz.getSimpleName(), new DataUtil<>(clazz, listener));
    }

}
