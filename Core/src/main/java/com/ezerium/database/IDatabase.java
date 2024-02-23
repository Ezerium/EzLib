package com.ezerium.database;

import org.jetbrains.annotations.NotNull;

public interface IDatabase {

    void connect();

    void disconnect();

    @NotNull
    String getURI();

}
