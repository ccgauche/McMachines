package com.ccgauche.mcmachines.handler.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public interface BlockInteractListener extends Listener {

	boolean onInteract(BlockInteract blockInteract);

	record BlockInteract(ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
			BlockHitResult blockHitResult) {

		public BlockState getBlockState() {
			return world.getBlockState(blockHitResult.getBlockPos());
		}

		public Block getBlock() {
			return getBlockState().getBlock();
		}
	}
}
