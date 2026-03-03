package com.dezxxx.hometasks.crud.repository.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private JsonUtil() {}

    public static <T> List<T> readList(Path path, Type listType) {
        ensureFileExists(path);

        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);

            if (json.isBlank()) {
                return new ArrayList<>();
            }

            List<T> data = GSON.fromJson(json, listType);
            return data != null ? data : new ArrayList<>();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    public static <T> void writeList(Path path, List<T> data, Type listType) {
        ensureFileExists(path);

        try {
            String json = GSON.toJson(data, listType);
            Files.writeString(
                    path,
                    json,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + path, e);
        }
    }

    private static void ensureFileExists(Path path) {
        try {
            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);

            if (!Files.exists(path)) {
                Files.createFile(path);
                Files.writeString(path, "", StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to init storage file: " + path, e);
        }
    }
}