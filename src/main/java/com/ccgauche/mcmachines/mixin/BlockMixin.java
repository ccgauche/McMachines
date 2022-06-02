package com.ccgauche.mcmachines.mixin;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.GlobalKeys;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
    private static void dropStack1(World world, BlockPos pos, ItemStack stack, CallbackInfo ci) {
        DataCompound compound = GlobalKeys.entries().get(pos);
        if (compound == null)
            return;
        compound.updateStack(stack);
        GlobalKeys.entries().remove(pos);
    }
    @Inject(method = "dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
    private static void dropStack1(World world, BlockPos pos, Direction direction, ItemStack stack, CallbackInfo ci) {
        DataCompound compound = GlobalKeys.entries().get(pos);
        if (compound == null)
            return;
        compound.updateStack(stack);
        GlobalKeys.entries().remove(pos);
    }
    @Inject(method = "afterBreak", at = @At("HEAD"))
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        DataCompound compound = GlobalKeys.entries().get(pos);
        if (compound == null)
            return;
        compound.updateStack(stack);
        GlobalKeys.entries().remove(pos);
    }

}
