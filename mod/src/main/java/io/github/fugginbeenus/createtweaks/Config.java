package io.github.fugginbeenus.createtweaks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Config instance;

    public boolean removeOreDoubling = true;
    public boolean removeUncraftableRecipes = true;
    public boolean hideUncraftableItems = true;
    public boolean deployerCannotAttack = true;
    public List<String> extraOres = new ArrayList<>();

    public static Config get() {
        if (instance == null) instance = load();
        return instance;
    }

    private static Config load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("createtweaks.json");
        try {
            if (Files.exists(path)) {
                Config loaded = GSON.fromJson(Files.readString(path), Config.class);
                if (loaded != null) return loaded;
            }
            Config fresh = new Config();
            Files.writeString(path, GSON.toJson(fresh));
            return fresh;
        } catch (IOException | RuntimeException e) {
            CreateTweaks.LOGGER.error("Could not read {}, using defaults", path, e);
            return new Config();
        }
    }
}
