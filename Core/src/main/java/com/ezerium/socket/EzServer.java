package com.ezerium.socket;

import com.ezerium.logger.EzLogger;

public interface EzServer {

    default void onStart() {
        EzLogger.log("Starting server on port " + getPort());
    }

    default int getPort() {
        return 2468;
    }

    void onReceive(EzSocket client);

    default void onStop() {
        EzLogger.log("Stopping server");
    }

}
