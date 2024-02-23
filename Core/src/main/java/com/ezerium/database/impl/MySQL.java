package com.ezerium.database.impl;

import com.ezerium.database.IDatabase;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;

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

    public MySQL createTable(String table) {
        return this;
    }
}
