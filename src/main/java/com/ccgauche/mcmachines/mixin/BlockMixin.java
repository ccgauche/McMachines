package com.ccgauche.mcmachines.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.registry.DataRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(Block.class)
public class BlockMixin {

	@Inject(method = "dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
	private static void dropStack1(World world, BlockPos pos, ItemStack stack, CallbackInfo ci) {
		unregisterMachine(world, pos, stack);
	}

	@Inject(method = "dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
	private static void dropStack1(World world, BlockPos pos, Direction direction, ItemStack stack, CallbackInfo ci) {
		unregisterMachine(world, pos, stack);
	}

	private static void unregisterMachine(World world, BlockPos pos, ItemStack stack) {
		var map = DataRegistry.getMap(world);
		DataCompound compound = map.get(pos);
		if (compound == null || stack.getItem() != Items.DROPPER || stack.getCount() != 1)
			return;
		compound.updateStack(stack);
		map.remove(pos);
	}

	@Inject(method = "afterBreak", at = @At("HEAD"))
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity,
			ItemStack stack, CallbackInfo ci) {
		unregisterMachine(world, pos, stack);
	}

}
