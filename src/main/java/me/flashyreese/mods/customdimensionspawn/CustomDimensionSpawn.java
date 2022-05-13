package me.flashyreese.mods.customdimensionspawn;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CustomDimensionSpawn implements ModInitializer, ServerPlayerEvents.AfterRespawn {

    public CustomDimensionSpawnLoader customDimensionSpawnLoader = new CustomDimensionSpawnLoader();
    
    @Override
    public void onInitialize() {
        this.customDimensionSpawnLoader.registerCommands();
        this.customDimensionSpawnLoader.loadDimensionSpawnPoints();
        ServerPlayerEvents.AFTER_RESPAWN.register(this);
    }

    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        Identifier oldPlayerDimension = oldPlayer.getWorld().getRegistryKey().getValue();
        this.customDimensionSpawnLoader.getDimensionSpawnPoints().forEach(dimensionSpawnPoint -> {
            if (dimensionSpawnPoint.getDimensionId() != null) {
                Identifier identifier = new Identifier(dimensionSpawnPoint.getDimensionId());
                if (oldPlayerDimension.equals(identifier)) {
                    newPlayer.teleport(newPlayer.server.getWorld(oldPlayer.getWorld().getRegistryKey()), dimensionSpawnPoint.getPositionX(), dimensionSpawnPoint.getPositionY(), dimensionSpawnPoint.getPositionZ(), dimensionSpawnPoint.getYaw(), dimensionSpawnPoint.getPitch());
                }
            }
        });
    }
}
