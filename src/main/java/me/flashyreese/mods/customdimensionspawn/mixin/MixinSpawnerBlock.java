package me.flashyreese.mods.customdimensionspawn.mixin;

import me.flashyreese.mods.customdimensionspawn.CustomDimensionSpawn;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnerBlock.class)
public class MixinSpawnerBlock {

    @Inject(method = "onStacksDropped", at = @At(value = "HEAD"), cancellable = true)
    public void customDimensionSpawn$onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, boolean dropExperience, CallbackInfo ci) {
        CustomDimensionSpawn.getCustomDimensionTools().getDimensionXPSettings().forEach((id, dropExperienceState) -> {
            if (world.getRegistryKey().getValue().equals(id) && !dropExperienceState) {
                ci.cancel();
            }
        });
    }
}
