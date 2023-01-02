/*
 * Copyright © 2022 FlashyReese
 *
 * This file is part of Custom Dimension Spawn.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.flashyreese.mods.customdimensionspawn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.flashyreese.mods.customdimensionspawn.model.DimensionSpawnPoint;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the custom dimension spawn point loader.
 *
 * @author FlashyReese
 */
public class CustomDimensionTools {

    private final Gson gson = new GsonBuilder().registerTypeAdapter(Identifier.class, new Identifier.Serializer()).create();
    private final List<DimensionSpawnPoint> dimensionSpawnPoints = new ArrayList<>();
    private final Map<Identifier, Boolean> dimensionXPSettings = new HashMap<>();
    private final Map<NbtCompound, Identifier> nbtAdvancementSettings = new HashMap<>();

    /**
     * Loads dimension spawn points file, meant for integrated/dedicated servers.
     */
    public void loadDimensionSpawnPoints() {
        this.dimensionSpawnPoints.clear();
        this.dimensionSpawnPoints.addAll(this.loadDimensionSpawnPoints(FabricLoader.getInstance().getConfigDir().resolve("custom-dimension-spawn.json").toFile()));

        this.dimensionXPSettings.clear();
        this.dimensionXPSettings.putAll(this.loadDimensionXPSettings(FabricLoader.getInstance().getConfigDir().resolve("custom-dimension-xp-settings.json").toFile()));

        this.nbtAdvancementSettings.clear();
        this.nbtAdvancementSettings.putAll(this.loadNbtAdvancementConditions(FabricLoader.getInstance().getConfigDir().resolve("custom-nbt-advancement-settings.json").toFile()));
    }
    /**
     * Registers all server Custom Dimension Spawn commands
     */
    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, registrationEnvironment) -> {
            dispatcher.register(CommandManager.literal("customdimensionspawn").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                    .executes(context -> {
                        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer("custom-dimension-spawn");
                        modContainerOptional.ifPresent(modContainer -> context.getSource().sendFeedback(Text.literal("Running Custom Dimension Spawn")
                                .formatted(Formatting.YELLOW)
                                .append(Text.literal(" v" + modContainer.getMetadata().getVersion()).formatted(Formatting.RED)), false));

                        return Command.SINGLE_SUCCESS;
                    })
                    .then(CommandManager.literal("reload").executes(context -> {
                        this.loadDimensionSpawnPoints();
                        return Command.SINGLE_SUCCESS;
                    }))
                    .then(CommandManager.literal("setspawnpoint").executes(context -> {
                        return Command.SINGLE_SUCCESS;
                    }).then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                            .then(CommandManager.argument("vec3", Vec3ArgumentType.vec3()).executes(context -> {
                                        return Command.SINGLE_SUCCESS;
                                    }).then(CommandManager.argument("vec2", Vec2ArgumentType.vec2()).executes(context -> {

                                        return Command.SINGLE_SUCCESS;
                                    }))
                            )).then(CommandManager.argument("vec3", Vec3ArgumentType.vec3()).executes(context -> {
                                return Command.SINGLE_SUCCESS;
                            }).then(CommandManager.argument("vec2", Vec2ArgumentType.vec2()).executes(context -> {

                                return Command.SINGLE_SUCCESS;
                            }))
                    ))
            );
        });
    }

    /**
     * Reads JSON file and serializes them to a List of DimensionSpawnPoint
     *
     * @param file JSON file path
     * @return List of DimensionSpawnPoint
     */
    private List<DimensionSpawnPoint> loadDimensionSpawnPoints(File file) {
        List<DimensionSpawnPoint> dimensionSpawnPoints = new ArrayList<>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                dimensionSpawnPoints = this.gson.fromJson(reader, new TypeToken<List<DimensionSpawnPoint>>() {
                }.getType());
            } catch (IOException e) {
                throw new RuntimeException("Could not parse DimensionSpawnPoint File", e);
            }
        } else {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                String json = this.gson.toJson(dimensionSpawnPoints);
                writer.write(json);
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException("Could not write DimensionSpawnPoint File", e);
            }
        }

        return dimensionSpawnPoints;
    }


    private Map<Identifier, Boolean> loadDimensionXPSettings(File file) {
        Map<Identifier, Boolean> dimensionXPSettings = new HashMap<>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                dimensionXPSettings = this.gson.fromJson(reader, new TypeToken<Map<Identifier, Boolean>>() {
                }.getType());
            } catch (IOException e) {
                throw new RuntimeException("Could not parse DimensionXPSettings File", e);
            }
        } else {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                String json = this.gson.toJson(dimensionXPSettings);
                writer.write(json);
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException("Could not write DimensionXPSettings File", e);
            }
        }

        return dimensionXPSettings;
    }

    private Map<NbtCompound, Identifier> loadNbtAdvancementConditions(File file) {
        Map<String, Identifier> nbtAdvancementSettings = new HashMap<>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                nbtAdvancementSettings = this.gson.fromJson(reader, new TypeToken<Map<String, Identifier>>() {
                }.getType());
            } catch (IOException e) {
                throw new RuntimeException("Could not parse DimensionXPSettings File", e);
            }
        } else {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                String json = this.gson.toJson(nbtAdvancementSettings);
                writer.write(json);
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException("Could not write DimensionXPSettings File", e);
            }
        }

        Map<NbtCompound, Identifier> nbtAdvancementConditions = nbtAdvancementSettings
                .entrySet()
                .stream()
                .collect(Collectors.toMap(keyMapper -> {
                    try {
                        return StringNbtReader.parse(keyMapper.getKey());
                    } catch (CommandSyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }, Map.Entry::getValue));

        return nbtAdvancementConditions;
    }

    public List<DimensionSpawnPoint> getDimensionSpawnPoints() {
        return dimensionSpawnPoints;
    }

    public Map<Identifier, Boolean> getDimensionXPSettings() {
        return dimensionXPSettings;
    }

    public Map<NbtCompound, Identifier> getNbtAdvancementSettings() {
        return nbtAdvancementSettings;
    }
}
