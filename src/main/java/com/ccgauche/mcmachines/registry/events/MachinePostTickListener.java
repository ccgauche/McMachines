package com.ccgauche.mcmachines.registry.events;

import com.ccgauche.mcmachines.data.DataCompound;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface MachinePostTickListener {
	void tick(World world, BlockPos pos, DataCompound compound);
}
