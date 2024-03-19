package com.ezerium.socket;

import com.google.gson.JsonObject;

public interface EzSocket {

    String getId();

    JsonObject getData();

    static EzSocket create(String id, JsonObject data) {
        return new EzSocket() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public JsonObject getData() {
                return data;
            }
        };
    }

}
