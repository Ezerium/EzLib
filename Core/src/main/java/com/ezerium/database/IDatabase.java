package com.ezerium.database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public interface IDatabase {

    Connection getConnection();

    void connect() throws SQLException;

    void disconnect();

    @NotNull
    String getURI();

    ResultSet execute(String query, Object... params) throws SQLException;

    void update(String query, Object... params) throws SQLException;

    CompletableFuture<ResultSet> executeAsync(String query, Object... params);

    CompletableFuture<Void> updateAsync(String query, Object... params);

}
