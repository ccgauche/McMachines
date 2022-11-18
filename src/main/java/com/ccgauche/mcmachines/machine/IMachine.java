package com.ccgauche.mcmachines.machine;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.registry.events.MachinePostTickListener;
import com.ccgauche.mcmachines.registry.events.MachineTickListener;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Abstract class for a machine
 */
public abstract class IMachine {

	public abstract String id();

	public void place(World world, BlockPos pos, DataCompound properties) {
		DataRegistry.ID.set(world, pos, this.id());
	}

	public abstract void tick(@NotNull DataCompound object, World world, BlockPos pos);

	public final List<MachineTickListener> preTickListeners = new ArrayList<>();
	public final List<MachinePostTickListener> postTickListeners = new ArrayList<>();

}
