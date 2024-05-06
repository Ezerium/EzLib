package com.ezerium;

import com.ezerium.annotations.Async;
import com.ezerium.annotations.Cache;
import com.ezerium.annotations.Debug;
import com.ezerium.annotations.Timer;
import com.ezerium.annotations.config.Configuration;
import com.ezerium.annotations.test.GeneratedTest;
import com.ezerium.logger.debug.DebugAt;
import com.ezerium.rabbitmq.MQData;
import com.ezerium.rabbitmq.MQHandler;
import com.ezerium.rabbitmq.MQListener;
import com.ezerium.rabbitmq.MQPacket;
import com.google.gson.JsonObject;

/**
 * Main class is mainly for testing purposes.
 */
public final class Main {

    public static void main(String[] args) {
    }

    public static class MyListener implements MQListener {
        @MQPacket("abc")
        public void onMessage(JsonObject object) {
            System.out.println("Received message: " + object);
        }

    }

}
