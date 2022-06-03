package com.ccgauche.mcmachines.handler.events;

import org.jetbrains.annotations.NotNull;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public interface PlayerTickListener extends Listener {

	void onTick(@NotNull ServerPlayerEntity player, @NotNull ServerWorld world, @NotNull ItemStack stack);
}
