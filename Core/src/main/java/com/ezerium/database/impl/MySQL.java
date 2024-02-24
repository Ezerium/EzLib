package com.ezerium.database.impl;

import com.ezerium.database.IDatabase;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

@Data
public class MySQL implements IDatabase {

    private final String host;
    private final String username;
    private final String password;
    private final String database;

    private boolean useSSL = false;

    @Setter(AccessLevel.NONE)
    private Connection connection;

    public MySQL(String address, int port, String username, String password, String database) {
        this(address + ":" + port, username, password, database);
    }

    public MySQL(String host, String username, String password, String database) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    @Override
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(this.getURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            this.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    @Override
    public String getURI() {
        return "jdbc:mysql://" + this.host + "/" + this.database + "?user=" + this.username + "&password=" + this.password + "&useSSL=" + this.useSSL;
    }

    @Override
    public ResultSet execute(String query, Object... params) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        for(int i = 0; i < params.length; i++) preparedStatement.setObject(i + 1, params[i]);

        return preparedStatement.executeQuery();
    }

    @Override
    public void update(String query, Object... params) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        for(int i = 0; i < params.length; i++) preparedStatement.setObject(i + 1, params[i]);

        preparedStatement.executeUpdate();
    }

    @Override
    public CompletableFuture<ResultSet> executeAsync(String query, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(query, params);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> updateAsync(String query, Object... params) {
        return CompletableFuture.runAsync(() -> {
            try {
                update(query, params);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void createTable(String tableName, String... columns) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (");

        for (int i = 0; i < columns.length; i++) {
            queryBuilder.append(columns[i]);
            if (i < columns.length - 1) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(")");
        update(queryBuilder.toString());
    }
}
