package com.ezerium.database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public interface IDatabase {

    void connect();

    void disconnect();

    @NotNull
    String getURI();
}
