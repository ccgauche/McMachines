package com.ccgauche.mcmachines.json.conditions;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.machine.IMachine;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record And(@NotNull List<ICondition> conditionList) implements ICondition {

	@Override
	public boolean isTrue(@NotNull IMachine machine, @NotNull ServerWorld world, @NotNull BlockPos pos,
			@NotNull DataCompound data) {
		return conditionList.stream().allMatch(e -> e.isTrue(machine, world, pos, data));
	}
}