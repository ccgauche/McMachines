package com.ccgauche.mcmachines.handler;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.handler.events.PlayerTickListener;
import com.ccgauche.mcmachines.registry.DataRegistry;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

/**
 * The magnetic handler enables plugins to add magnetic effects to items,
 * players,
 */
public class Magnetic implements PlayerTickListener {
	@Override
	public String handlerId() {
		return "magnetic";
	}

	@Override
	public void onTick(@NotNull ServerPlayerEntity player, @NotNull ServerWorld world, @NotNull ItemStack stack) {
		DataCompound compound = new DataCompound(stack);
		int energy = DataRegistry.ENERGY_CONTENT.getOrDefault(compound, 0);
		if (energy == 0)
			return;
		boolean changed = false;
		for (ItemEntity entity : world.getEntitiesByClass(ItemEntity.class,
				new Box(player.getX() - 5.0, player.getY() - 5.0, player.getZ() - 5.0, player.getX() + 5.0,
						player.getY() + 5.0, player.getZ() + 5.0),
				e -> true)) {
			if (energy == 0)
				return;
			DataRegistry.ENERGY_CONTENT.set(compound, energy - 1);
			energy -= 1;
			changed = true;
			entity.teleport(player.getX(), player.getY(), player.getZ());
		}
		if (changed)
			compound.updateStack(stack);
	}
}
