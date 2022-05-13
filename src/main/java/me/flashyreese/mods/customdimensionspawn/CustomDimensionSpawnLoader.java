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
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.Command;
import me.flashyreese.mods.customdimensionspawn.model.DimensionSpawnPoint;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the custom dimension spawn point loader.
 *
 * @author FlashyReese
 */
public class CustomDimensionSpawnLoader {

    private final Gson gson = new Gson();
    private final List<DimensionSpawnPoint> dimensionSpawnPoints = new ArrayList<>();

    /**
     * Loads dimension spawn points file, meant for integrated/dedicated servers.
     */
    public void loadDimensionSpawnPoints() {
        this.dimensionSpawnPoints.clear();
        this.dimensionSpawnPoints.addAll(this.loadDimensionSpawnPoints(new File("config/custom-dimension-spawn.json")));
    }

    /**
     * Registers all server Custom Dimension Spawn commands
     */
    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("customdimensionspawn").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                    .executes(context -> {
                        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer("custom-dimension-spawn");
                        modContainerOptional.ifPresent(modContainer -> context.getSource().sendFeedback(new LiteralText("Running Custom Dimension Spawn")
                                .formatted(Formatting.YELLOW)
                                .append(new LiteralText(" v" + modContainer.getMetadata().getVersion()).formatted(Formatting.RED)), false));

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
                String json = this.gson.toJson(new ArrayList<>());
                writer.write(json);
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException("Could not write DimensionSpawnPoint File", e);
            }
        }

        return dimensionSpawnPoints;
    }

    public List<DimensionSpawnPoint> getDimensionSpawnPoints() {
        return dimensionSpawnPoints;
    }
}
