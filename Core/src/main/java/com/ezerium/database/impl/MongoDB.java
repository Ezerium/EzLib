package com.ezerium.database.impl;

import com.ezerium.database.IDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Data
public class MongoDB implements IDatabase {

    @Setter(AccessLevel.NONE)
    private MongoClient client;
    @Setter(AccessLevel.NONE)
    private MongoDatabase mongoDatabase;

    private final String host;
    private final String username;
    private final String password;
    private final String database;

    private boolean retryWrites = true;
    private boolean autoReconnect = true;
    private String w = "majority";

    private final Map<String, MongoCollection<Document>> collections = new HashMap<>();

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
        this.client = MongoClients.create(this.getURI());
        this.mongoDatabase = this.client.getDatabase(this.database);
    }

    @Override
    public void disconnect() {
        this.client.close();
        this.mongoDatabase = null;
        this.client = null;
    }

    @NotNull
    @Override
    public String getURI() {
        return "mongodb" + (host.split(":").length > 1 ? "" : "+srv") + "://" + this.username + ":" + this.password + "@" + this.host + "/" + this.database + "?w=" + this.w + "&retryWrites=" + this.retryWrites + "&autoReconnect=" + this.autoReconnect;
    }

    public MongoDB addCollection(String name) {
        this.collections.put(name, this.mongoDatabase.getCollection(name));
        return this;
    }

    public MongoCollection<Document> getCollection(String name) {
        return this.collections.get(name);
    }

    public CompletableFuture<Void> insertAsync(String collection, Document document) {
        return CompletableFuture.runAsync(() -> this.collections.get(collection).insertOne(document));
    }

    public CompletableFuture<Void> updateAsync(String collection, Document filter, Document update) {
        return CompletableFuture.runAsync(() -> this.collections.get(collection).updateOne(filter, update));
    }

    public CompletableFuture<Void> replaceAsync(String collection, Document filter, Document replacement) {
        return CompletableFuture.runAsync(() -> this.collections.get(collection).replaceOne(filter, replacement));
    }

    public CompletableFuture<Void> deleteAsync(String collection, Document filter) {
        return CompletableFuture.runAsync(() -> this.collections.get(collection).deleteOne(filter));
    }

    public CompletableFuture<Document> findAsync(String collection, Document filter) {
        return CompletableFuture.supplyAsync(() -> this.collections.get(collection).find(filter).first());
    }

    public CompletableFuture<Void> insertManyAsync(String collection, Document... documents) {
        return CompletableFuture.runAsync(() -> this.collections.get(collection).insertMany(Arrays.asList(documents)));
    }

    public CompletableFuture<Void> updateManyAsync(String collection, Document filter, Document update) {
        return CompletableFuture.runAsync(() -> this.collections.get(collection).updateMany(filter, update));
    }

    public CompletableFuture<Void> deleteManyAsync(String collection, Document filter) {
        return CompletableFuture.runAsync(() -> this.collections.get(collection).deleteMany(filter));
    }

    public Document find(String collection, Document filter) {
        return this.collections.get(collection).find(filter).first();
    }

    public void insert(String collection, Document document) {
        this.collections.get(collection).insertOne(document);
    }

    public void update(String collection, Document filter, Document update) {
        this.collections.get(collection).updateOne(filter, update);
    }

    public void replace(String collection, Document filter, Document replacement) {
        this.collections.get(collection).replaceOne(filter, replacement);
    }

    public void delete(String collection, Document filter) {
        this.collections.get(collection).deleteOne(filter);
    }

    public void insertMany(String collection, Document... documents) {
        this.collections.get(collection).insertMany(Arrays.asList(documents));
    }

    public void updateMany(String collection, Document filter, Document update) {
        this.collections.get(collection).updateMany(filter, update);
    }

    public void deleteMany(String collection, Document filter) {
        this.collections.get(collection).deleteMany(filter);
    }

}
