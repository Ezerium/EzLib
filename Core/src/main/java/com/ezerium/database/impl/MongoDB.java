package com.ezerium.database.impl;

import com.ezerium.database.IDatabase;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class MongoDB implements IDatabase {

    private final String host;
    private final String username;
    private final String password;
    private final String database;

    private String w = "majority";

    public MongoDB(String address, int port, String username, String password, String database) {
        this(address + ":" + port, username, password, database);
    }

    public MongoDB(String host, String username, String password, String database) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @NotNull
    @Override
    public String getURI() {
        return "mongodb" + (host.split(":").length > 1 ? "+srv" : "") + "://" + this.host + "@" + username + ":" + password + "/" + this.database;
    }
}
