package com.ccgauche.mcmachines.json.conditions;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.machine.IMachine;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record Relative(int x, int y, int z, @NotNull ICondition condition) implements ICondition {
	@Override
	public boolean isTrue(@NotNull IMachine machine, @NotNull ServerWorld world, @NotNull BlockPos pos,
			@NotNull DataCompound data) {
		return condition.isTrue(machine, world, pos.add(x, y, z), data);
	}
}
