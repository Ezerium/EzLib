package com.ezerium.rabbitmq;

import com.google.gson.JsonObject;

public interface MQData {

    String getName();

    JsonObject getData();

    static MQData create(String name, JsonObject data) {
        return new MQData() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public JsonObject getData() {
                return data;
            }
        };
    }
}
