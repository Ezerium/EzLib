package com.ezerium.shared.config;

import javafx.beans.value.ChangeListener;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
public class ConfigData {

    private final String name;
    private final Class<?> clazz;

    private final Map<String, ChangeListener<?>> values;

    public ConfigData(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
        this.values = new HashMap<>();
    }

    public void addValue(Field field) {
    }

}
