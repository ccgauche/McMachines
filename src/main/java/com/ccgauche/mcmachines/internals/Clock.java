package com.ccgauche.mcmachines.internals;

import java.util.HashMap;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.machine.IMachine;
import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.registry.MachineRegistry;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Clock {

	@SuppressWarnings("all")
	public static void tick(@NotNull World world) {
		for (var pos : ((HashMap<@NotNull BlockPos, DataCompound>) (DataRegistry.getMap(world).clone())).entrySet()) {
			String orDefault = DataRegistry.ID.getOrDefault(pos.getValue(), "");
			IMachine machine = MachineRegistry.get(orDefault);
			if (machine != null) {
				machine.tick(pos.getValue(), world, pos.getKey());
			}
		}
	}
}
