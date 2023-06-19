package de.miraculixx.bmbm.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class MixinLevel {

    @Inject(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;onBlockStateChange(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V"
        )
    )
    private void onBlockUpdate(BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        Item block = blockState.getBlock().asItem();
        if (((Level) (Object) this).getBlockState(blockPos).getBlock().asItem() == Items.AIR && block == Items.AIR) {
            System.out.println(block + " - " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ() + "");
        }
    }
}
