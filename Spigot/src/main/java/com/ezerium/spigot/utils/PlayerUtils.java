package com.ezerium.spigot.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@UtilityClass
public class PlayerUtils {

    public static JsonObject getProfileSigned(UUID uuid) {
        String uuidString = uuid.toString().replace("-", "");

        try {
            InputStream is = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString + "?unsigned=false").openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);

            JsonElement element = new JsonParser().parse(jsonText);
            if (element == null || element.isJsonNull()) return null;

            return (JsonObject) element;
        } catch (Exception e) {
            return null;
        }
    }

    public static UUID getUUID(String name) {

        try {
            InputStream is = new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);

            JsonElement element = new JsonParser().parse(jsonText);
            if (element == null || element.isJsonNull()) return null;

            String uuidString = ((JsonObject) element).get("id").getAsString();
            return UUID.fromString(
                    uuidString.substring(0, 8) + "-" + uuidString.substring(8, 12) + "-"
                            + uuidString.substring(12, 16) + "-" + uuidString.substring(16, 20) + "-" + uuidString.substring(20)
            );
        } catch (Exception e) {
            return null;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}