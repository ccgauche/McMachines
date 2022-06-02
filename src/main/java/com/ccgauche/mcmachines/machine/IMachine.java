package com.ccgauche.mcmachines.machine;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.registry.DataRegistry;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMachine {

	String id();

	default void place(World world, BlockPos pos, DataCompound properties) {
		DataRegistry.ID.set(world, pos, this.id());
	}

	void tick(@NotNull DataCompound object, World world, BlockPos pos);
}
