package com.ccgauche.mcmachines.json.conditions;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.machine.IMachine;

import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record Type(@NotNull Block block) implements ICondition {

	@Override
	public boolean isTrue(@NotNull IMachine machine, @NotNull ServerWorld world, @NotNull BlockPos pos,
			@NotNull DataCompound data) {
		return world.getBlockState(pos).getBlock() == block;
	}
}
