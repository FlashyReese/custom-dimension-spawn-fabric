package me.flashyreese.mods.customdimensionspawn;

import me.flashyreese.mods.customdimensionspawn.model.CoordinateRange;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CustomDimensionSpawn implements ModInitializer, ServerPlayerEvents.AfterRespawn, PlayerBlockBreakEvents.After {

    private static final CustomDimensionTools CUSTOM_DIMENSION_TOOLS = new CustomDimensionTools();
    
    @Override
    public void onInitialize() {
        CUSTOM_DIMENSION_TOOLS.registerCommands();
        CUSTOM_DIMENSION_TOOLS.loadDimensionSpawnPoints();
        ServerPlayerEvents.AFTER_RESPAWN.register(this);
        PlayerBlockBreakEvents.AFTER.register(this);
    }

    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        Identifier oldPlayerDimension = oldPlayer.getWorld().getRegistryKey().getValue();
        CUSTOM_DIMENSION_TOOLS.getDimensionSpawnPoints().forEach(dimensionSpawnPoint -> {
            Identifier identifier = dimensionSpawnPoint.getDimensionId();

            Identifier dimensionRespawn = dimensionSpawnPoint.getDimensionRespawn() == null ? oldPlayer.getWorld().getRegistryKey().getValue() : dimensionSpawnPoint.getDimensionRespawn();

            boolean checkDimension = dimensionSpawnPoint.getDimensionId() == null || oldPlayerDimension.equals(identifier);

            ServerWorld serverWorld = null;
            for (ServerWorld world : newPlayer.server.getWorlds()) {
                if (world.getRegistryKey().getValue().equals(dimensionRespawn)) {
                    serverWorld = world;
                    break;
                }
            }

            if (serverWorld == null) {
                System.out.println("Something went wrong, couldn't find world with respawn dimension");
                return;
            }

            // Coordinates range check
            List<CoordinateRange> coordinateRangeList = dimensionSpawnPoint.getCoordinateRangeList().stream().filter(data -> data.isWithinRange(oldPlayer.getPos())).toList();

            if (!coordinateRangeList.isEmpty() && checkDimension) { // Is within dimension of
                newPlayer.teleport(serverWorld, dimensionSpawnPoint.getPositionX(), dimensionSpawnPoint.getPositionY(), dimensionSpawnPoint.getPositionZ(), dimensionSpawnPoint.getYaw(), dimensionSpawnPoint.getPitch());
            }

            // Biome check
            boolean wasInBiome = dimensionSpawnPoint.getBiomeList().contains(oldPlayer.world.getRegistryManager().get(RegistryKeys.BIOME).getId(oldPlayer.world.getBiome(oldPlayer.getBlockPos()).value()));

            if (wasInBiome && checkDimension) {
                newPlayer.teleport(serverWorld, dimensionSpawnPoint.getPositionX(), dimensionSpawnPoint.getPositionY(), dimensionSpawnPoint.getPositionZ(), dimensionSpawnPoint.getYaw(), dimensionSpawnPoint.getPitch());
            }

            if (checkDimension) {
                newPlayer.teleport(serverWorld, dimensionSpawnPoint.getPositionX(), dimensionSpawnPoint.getPositionY(), dimensionSpawnPoint.getPositionZ(), dimensionSpawnPoint.getYaw(), dimensionSpawnPoint.getPitch());
            }
        });
    }

    @Override
    public void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity == null) {
            return;
        }

        NbtCompound blockEntityNbt = blockEntity.createNbt();

        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            this.checkNbtConditions(blockEntityNbt).forEach(identifier -> {
                Advancement advancement = Objects.requireNonNull(serverPlayerEntity.getServer()).getAdvancementLoader().get(identifier);

                if (advancement == null) {
                    System.out.println("Something went wrong, couldn't find world with respawn dimension");
                    return;
                }

                AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementTracker().getProgress(advancement);
                if (!advancementProgress.isDone()) {
                    for (String obtainedCriterion : advancementProgress.getUnobtainedCriteria()) {
                        serverPlayerEntity.getAdvancementTracker().grantCriterion(advancement, obtainedCriterion);
                    }
                }
            });
        }
    }

    public List<Identifier> checkNbtConditions(NbtCompound nbtCompound) {
        List<Identifier> grantAdvancementList = new ArrayList<>();

        for (Map.Entry<NbtCompound, Identifier> nbtCompoundIdentifierEntry : CUSTOM_DIMENSION_TOOLS.getNbtAdvancementSettings().entrySet()) {
            NbtCompound entryKey = nbtCompoundIdentifierEntry.getKey();
            Identifier entryValue = nbtCompoundIdentifierEntry.getValue();

            if (NbtUtils.compareNbtCompound(entryKey, nbtCompound)) {
                grantAdvancementList.add(entryValue);
            }
        }

        return grantAdvancementList;
    }

    public static CustomDimensionTools getCustomDimensionTools() {
        return CUSTOM_DIMENSION_TOOLS;
    }

}
